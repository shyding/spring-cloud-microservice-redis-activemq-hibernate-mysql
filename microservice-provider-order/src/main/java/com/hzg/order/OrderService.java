package com.hzg.order;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.User;
import com.hzg.erp.*;
import com.hzg.pay.Pay;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class OrderService {
    Logger logger = Logger.getLogger(OrderService.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private PayClient payClient;

    @Autowired
    private ErpClient erpClient;

    @Autowired
    private Writer writer;

    @Autowired
    public ObjectToSql objectToSql;

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    private DateUtil dateUtil;

    /**
     * 根据订单类型保存订单
     * @param order
     * @return
     */
    public String saveOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveOrder start");

        String isAmountRight = checkAmount(order);
        if (!isAmountRight.equals("")) {
            return CommonConstant.fail + isAmountRight;
        }

        String canSellMsg = isCanSell(order);
        if (!canSellMsg.equals("")) {
            return CommonConstant.fail + canSellMsg;
        }

        order.setNo(orderDao.getNo(OrderConstant.no_order_perfix));
        result += lockOrderProduct(order);

        order.setState(OrderConstant.order_state_unPay);
        order.setDate(dateUtil.getSecondCurrentTimestamp());

        if (order.getType().compareTo(OrderConstant.order_type_selfService) == 0) {
            order.setUser((User) orderDao.getFromRedis((String) orderDao.getFromRedis(
                    CommonConstant.sessionId + CommonConstant.underline + order.getSessionId())));

            result += saveBaseOrder(order);

            Pay pay = new Pay();
            pay.setAmount(order.getPayAmount());

            result += saveOrderPay(pay, order);

        } else {
            if (order.getType().compareTo(OrderConstant.order_type_assist) == 0) {
                result += saveBaseOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                result += saveAssistProcessOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                result += savePrivateOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                result += saveBookOrder(order);
            }

            /**
             * 保存前台传递过来的代下单支付记录(支付、收款账号,支付金额等信息)
             */
            for (Pay pay : order.getPays()) {
                pay.setPayDate(dateUtil.getSecondCurrentTimestamp());

                result += saveOrderPay(pay, order);
            }
        }


        logger.info("saveOrder end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

    }

    /**
     * 检查金额是否正确
     * @param order
     * @return
     */
    public String checkAmount(Order order) {
        String result = "";

        BigDecimal amount = new BigDecimal(0);

        for (OrderDetail detail : order.getDetails()) {
            String queryJson = "{\"" + ErpConstant.product + "\":{\"id\":" + detail.getProduct().getId() + "}";
            if (detail.getPriceChange() != null) {
                queryJson += ",\"" + CommonConstant.id +"\":\"" + detail.getPriceChange().getId() + "\"," +
                        "\"" + CommonConstant.state +"\":" + ErpConstant.product_price_change_state_use + "}";
            } else {
                queryJson += "}";
            }

            Map<String, Float> salePrice = writer.gson.fromJson(erpClient.querySalePrice(queryJson), new TypeToken<Map<String, Float>>(){}.getType());
            BigDecimal detailAmount = new BigDecimal(Float.toString(salePrice.get(ErpConstant.price))).
                    multiply(new BigDecimal(Float.toString(detail.getQuantity())));

            if (detailAmount.floatValue() != detail.getPayAmount() || detailAmount.floatValue() == 0f) {
                result += "商品" + detail.getProduct().getNo() + "支付金额不对;";
            } else {
                amount = amount.add(detailAmount);
            }
        }

        if (amount.floatValue() != order.getPayAmount()) {
            result =  "订单支付金额不对";
        }

        return result;
    }

    public String saveBaseOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveSelfServiceOrder start:" + result);

        BigDecimal discount = new BigDecimal(Float.toString(order.getPayAmount())).
                divide(new BigDecimal(Float.toString(order.getAmount())), 2, BigDecimal.ROUND_HALF_UP);
        order.setDiscount(discount.floatValue());
        result += orderDao.save(order);

        Order idOrder = new Order();
        idOrder.setId(order.getId());

        for (OrderDetail detail : order.getDetails()) {
            detail.setOrder(idOrder);
            detail.setDate(dateUtil.getSecondCurrentTimestamp());

            BigDecimal detailDiscount = new BigDecimal(Float.toString(detail.getPayAmount())).
                    divide(new BigDecimal(Float.toString(detail.getAmount())), 2, BigDecimal.ROUND_HALF_UP);
            detail.setDiscount(detailDiscount.floatValue());


            if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                detail.setState(OrderConstant.order_detail_state_book);
            } else {
                detail.setState(OrderConstant.order_detail_state_unSale);
            }

            result += orderDao.save(detail);
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                gift.setOrder(order);
                result += orderDao.save(gift);
            }
        }

        logger.info("saveSelfServiceOrder end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveAssistProcessOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveAssistProcessOrder start:" + result);

        result += saveBaseOrder(order);

        for (OrderDetail detail : order.getDetails()) {
            OrderDetail idDetail = new OrderDetail();
            idDetail.setId(detail.getId());
            detail.getOrderPrivate().setDetail(idDetail);

            if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                detail.getOrderPrivate().setType(OrderConstant.order_private_type_process);

            } else if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                detail.getOrderPrivate().setType(OrderConstant.order_private_type_customize);
            }

            result += orderDao.save(detail.getOrderPrivate());
        }

        logger.info("saveAssistProcessOrder end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String savePrivateOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("savePrivateOrder start:" + result);

        result += saveAssistProcessOrder(order);

        for (OrderDetail detail : order.getDetails()) {
            OrderPrivate idOrderPrivate = new OrderPrivate();
            idOrderPrivate.setId(detail.getOrderPrivate().getId());

            for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {
                acc.setOrderPrivate(idOrderPrivate);
                result += orderDao.save(acc);
            }
        }

        logger.info("savePrivateOrder end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveBookOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveBookService start:" + result);

        result += saveBaseOrder(order);

        Order idOrder = new Order();
        idOrder.setId(order.getId());

        order.getOrderBook().setOrder(idOrder);
        result += orderDao.save(order.getOrderBook());

        logger.info("saveBookService end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveOrderPay(Pay pay, Order order) {
        String result = CommonConstant.fail;

        logger.info("savePay start:" + result);
        pay.setState(PayConstants.state_pay_apply);

        pay.setEntity(order.getClass().getSimpleName().toLowerCase());
        pay.setEntityId(order.getId());
        pay.setEntityNo(order.getNo());

        Map<String, String> result1 = writer.gson.fromJson(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
        result = result1.get(CommonConstant.result);

        logger.info("savePay end, result:" + result);

        return result;
    }

    /**
     * 检查是否可销售
     * @param order
     * @return
     */
    public String isCanSell(Order order) {
        String canSellMsg = "";

        for (OrderDetail detail : order.getDetails()) {
            Float sellableQuantity = getOnSaleQuantity(detail.getProduct().getNo());

            if (sellableQuantity.compareTo(detail.getQuantity()) < 0) {
                canSellMsg += detail.getQuantity() + detail.getUnit() + "编号为:" + detail.getProduct().getNo() +
                        "的商品，但该商品可售数量为：" + sellableQuantity + ";";
            }
        }

        if (!canSellMsg.equals("")) {
            canSellMsg = "尊敬的顾客你好，你预定了:" + canSellMsg + "预定失败。如有帮助需要，请联系我公司客服人员处理";
        }

        return canSellMsg;
    }

    /**
     * 锁住订单里的商品
     * @param order
     * @return
     */
    public String lockOrderProduct(Order order) {
        for (OrderDetail detail : order.getDetails()) {
            int lockTime = OrderConstant.order_session_time;

            if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                if (order.getOrderBook().getDeposit().compareTo(order.getAmount()/2) >= 0) {
                    lockTime = OrderConstant.order_book_deposit_notLess_half_product_lock_time;
                } else {
                    lockTime = OrderConstant.order_book_deposit_less_half_product_lock_time;
                }
            }

            String key = detail.getProduct().getNo() + CommonConstant.underline + order.getNo();
            orderDao.storeToRedis(key, detail.getQuantity(), lockTime);
            orderDao.putKeyToList(OrderConstant.lock_product_quantity + CommonConstant.underline + detail.getProduct().getNo(), key);
        }

        return CommonConstant.success;
    }

    /**
     * 获取可销售数量
     * @param productNo
     * @return
     */
    public Float getOnSaleQuantity(String productNo) {
        Map<String, Float> stockQuantity = writer.gson.fromJson(erpClient.getStockQuantity("{\"" + ErpConstant.product_no + "\":\"" + productNo +"\"}"),
                new com.google.gson.reflect.TypeToken<Map<String, Float>>() {}.getType());
        List<Object> lockQuantities = orderDao.getValuesFromList(OrderConstant.lock_product_quantity + CommonConstant.underline + productNo);

        Float sellableQuantity = stockQuantity.get(ErpConstant.stock_quantity);
        for (Object lockQuantity : lockQuantities) {
            sellableQuantity -= (Float)lockQuantity;
        }

        return sellableQuantity;
    }

    /**
     * 取消订单
     * @param order
     */
    public String cancelOrder(Order order) {
        String result;

        for (OrderDetail detail : order.getDetails()) {
            orderDao.deleteFromRedis(detail.getProduct().getNo() + CommonConstant.underline + order.getNo());
        }

        order.setState(OrderConstant.order_state_cancel);
        result = orderDao.updateById(order.getId(), order);

        return result;
    }

    /**
     * 确认订单已付款
     * @param order
     */
    public String paidOrder(Order order) {
        String result = CommonConstant.fail;

        /**
         * 移除订购时锁定的商品
         */
        for (OrderDetail detail : order.getDetails()) {
            orderDao.deleteFromRedis(detail.getProduct().getNo() + CommonConstant.underline + order.getNo());
        }

        for (OrderDetail detail : order.getDetails()) {
            detail.setState(OrderConstant.order_detail_state_saled);
            result += orderDao.updateById(detail.getId(), detail);

            orderDao.deleteFromRedis(detail.getProduct().getNo() + CommonConstant.underline + order.getNo());
        }

        order.setState(OrderConstant.order_state_paid_confirm);
        result += orderDao.updateById(order.getId(), order);

        Pay pay = new Pay();
        pay.setEntity(order.getClass().getSimpleName().toLowerCase());
        pay.setEntityId(order.getId());
        List<Pay> pays = writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new TypeToken<List<Pay>>() {}.getType());

        for (Pay ele : pays) {
            ele.setState(PayConstants.state_pay_success);
            result += payClient.update(ele.getClass().getSimpleName(), writer.gson.toJson(ele));
        }

        result += setProductsSold(order);
        result += stockOut(order);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 审核通过自助单
     * @param order
     */
    public String audit(Order order) {
        String result = CommonConstant.fail;

        /**
         * 移除订购时锁定的商品
         */
        for (OrderDetail detail : order.getDetails()) {
            orderDao.deleteFromRedis(detail.getProduct().getNo() + CommonConstant.underline + order.getNo());
        }

        order.setState(OrderConstant.order_state_paid_confirm);
        result += orderDao.updateById(order.getId(), order);
        result += setProductsSold(order);
        result += stockOut(order);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    private String setProductsSold(Order order) {
        List<Product> products = new ArrayList<>();
        for (OrderDetail detail : order.getDetails()) {
            products.add(detail.getProduct());
        }

        return erpClient.business("setProductsSold", writer.gson.toJson(products));
    }

    /**
     * 插入出库数据
     */
    private String stockOut(Order order) {
        StockInOut stockInOut = new StockInOut();
        stockInOut.setNo(((Map<String, String>)writer.gson.fromJson(erpClient.getNo(ErpConstant.no_stockOut_perfix), new TypeToken<Map<String, String>>() {}.getType())).get(CommonConstant.no));
        stockInOut.setType(ErpConstant.stockInOut_type_normal_outWarehouse);

        stockInOut.setState(ErpConstant.stockInOut_state_finished);
        stockInOut.setDate(dateUtil.getSecondCurrentTimestamp());
        stockInOut.setInputDate(dateUtil.getSecondCurrentTimestamp());
        stockInOut.setDescribes("销售订单：" + order.getNo() + "支付完成，货品自动出库");

        Set<StockInOutDetail> stockInOutDetails = new HashSet<>();
        for (OrderDetail detail : order.getDetails()) {
            StockInOutDetail stockInOutDetail = new StockInOutDetail();
            stockInOutDetail.setProduct(detail.getProduct());
            stockInOutDetail.setQuantity(detail.getQuantity());
            stockInOutDetail.setUnit(detail.getUnit());
            stockInOutDetails.add(stockInOutDetail);
        }

        stockInOut.setDetails(stockInOutDetails);

        return  erpClient.save(stockInOut.getClass().getSimpleName(), writer.gson.toJson(stockInOut));
    }

    /**
     * 每隔 2 个小时，查询出订金 < 50% 的预定订单，如果这些订单未支付时间超过 2 天，则修改订单状态为取消状态
     */
    @Scheduled(cron = "0 0 0/" + OrderConstant.order_session_time/CommonConstant.hour_seconds + " * * ?")
    public void clearMap(){
        Map<String, String> parameters = new HashMap<String, String>();

        String currentDay = dateUtil.getCurrentDayStr();
        parameters.put(OrderConstant.order_class_field_date, currentDay + " - " + currentDay);
        parameters.put(OrderConstant.order_class_field_state, String.valueOf(OrderConstant.order_detail_state_unSale));
        parameters.put(OrderConstant.order_class_field_type, String.valueOf(OrderConstant.order_type_book));

        List<Order> orders = orderDao.complexQuery(Order.class, parameters, 0, -1);

        long currentTimeMillis = System.currentTimeMillis();
        for (Order order : orders) {
            if ((currentTimeMillis - order.getDate().getTime())/1000 > OrderConstant.order_book_deposit_less_half_product_lock_time) {
                order.setState(OrderConstant.order_state_cancel);
                orderDao.updateById(order.getId(), order);
            }
        }
    }

    public User getSignUser(String json) {
        Map<String, Object> jsonData = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        User user = (User) orderDao.getFromRedis((String) orderDao.getFromRedis(CommonConstant.sessionId +
                CommonConstant.underline + (String) jsonData.get(CommonConstant.sessionId)));

        User simpleUser = new User();
        simpleUser.setId(user.getId());

        return simpleUser;
    }

    public List privateQuery(String entity, String json, int position, int rowNum) {
        if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            Class[] clazzs = {Order.class, OrderDetail.class, Product.class, OrderPrivate.class};
            Map<String, List<Object>> results = orderDao.queryBySql(getOrderPrivateSql(json, position, rowNum), clazzs);

            List<Object> orders = results.get(Order.class.getName());
            List<Object> details = results.get(OrderDetail.class.getName());
            List<Object> products = results.get(Product.class.getName());
            List<Object> orderPrivates = results.get(OrderPrivate.class.getName());

            int i = 0;
            for (Object detail : details) {
                OrderPrivate orderPrivate = (OrderPrivate)orderPrivates.get(i);
                if (orderPrivate.getAuthorize() != null) {
                    orderPrivate.setAuthorize((OrderPrivateAuthorize) orderDao.queryById(orderPrivate.getAuthorize().getId(), orderPrivate.getAuthorize().getClass()));
                }

                ((OrderDetail)detail).setOrderPrivate(orderPrivate);
                ((OrderDetail)detail).setOrder((Order)orders.get(i));
                ((OrderDetail)detail).setProduct((Product)products.get(i));

                i++;
            }

            return details;
        }

        return null;
    }

    public BigInteger privateRecordNum(String entity, String json){
        String sql = "";

        if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            sql = getOrderPrivateSql(json, 0, -1);
        }

        sql = "select count(t.id) from " + sql.split(" from ")[1];
        return (BigInteger)sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();
    }

    public String getOrderPrivateSql(String json, int position, int rowNum) {
        String sql = "";

        try {
            String orderSql = objectToSql.generateComplexSqlByAnnotation(Order.class,
                    writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType()), position, rowNum);
            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = orderDao.getSqlPart(orderSql, Order.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];

            selectSql += ", " + orderDao.getSelectColumns("t2", OrderDetail.class);
            fromSql += ", " + objectToSql.getTableName(OrderDetail.class) + " t2 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t2." + objectToSql.getColumn(OrderDetail.class.getDeclaredField("order")) +
                    " = t." + objectToSql.getColumn(Order.class.getDeclaredField("id"));

            selectSql += ", " + orderDao.getSelectColumns("t21", Product.class);
            fromSql += ", " + objectToSql.getTableName(Product.class) + " t21 ";
            whereSql += " and t21." + objectToSql.getColumn(Product.class.getDeclaredField("id")) +
                    " = t2." + objectToSql.getColumn(OrderDetail.class.getDeclaredField("product"));

            selectSql += ", " + orderDao.getSelectColumns("t3", OrderPrivate.class);
            fromSql += ", " + objectToSql.getTableName(OrderPrivate.class) + " t3 ";
            whereSql += " and t3." + objectToSql.getColumn(OrderPrivate.class.getDeclaredField("detail")) +
                    " = t2." + objectToSql.getColumn(OrderDetail.class.getDeclaredField("id"));

            sql = "select " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }
}
