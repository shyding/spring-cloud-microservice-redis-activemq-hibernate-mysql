package com.hzg.order;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.User;
import com.hzg.pay.Pay;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        logger.info("saveOrder start:" + result);

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

        if (order.getType().compareTo(OrderConstant.order_type_selfService) == 0 ||
                order.getType().compareTo(OrderConstant.order_type_assist) == 0) {
            order.setUser((User) orderDao.getFromRedis((String) orderDao.getFromRedis(
                    CommonConstant.sessionId + CommonConstant.underline + order.getSessionId())));

            result += saveBaseOrder(order);

        } else if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
            result += saveAssistProcessOrder(order);

        } else if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
            result += savePrivateOrder(order);

        } else if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
            result += saveBookOrder(order);
        }

        result += saveOrderPay(order);

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

        result += orderDao.save(order);

        Order idOrder = new Order();
        idOrder.setId(order.getId());

        for (OrderDetail detail : order.getDetails()) {
            detail.setOrder(idOrder);
            detail.setDate(dateUtil.getSecondCurrentTimestamp());

            if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                detail.setState(OrderConstant.order_detail_state_book);
            } else {
                detail.setState(OrderConstant.order_detail_state_unSale);
            }

            result += orderDao.save(detail);
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

    public String saveOrderPay(Order order) {
        String result = CommonConstant.fail;

        logger.info("savePay start:" + result);

        Pay pay = new Pay();
        pay.setAmount(order.getAmount());
        pay.setState(PayConstants.state_pay_apply);

        pay.setEntity(order.getClass().getSimpleName().toLowerCase());
        pay.setEntityId(order.getId());
        pay.setEntityNo(order.getNo());

        pay.setAmount(order.getPayAmount());

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

/*    *//**
     * 每隔 2 个小时，取消 2 小时还未支付的订单
     *//*
    @Scheduled(cron = "0 0 0/" + OrderConstant.order_session_time/CommonConstant.hour_seconds + " * * ?")
    public void clearMap(){
        Map<String, String> parameters = new HashMap<String, String>();

        String currentDay = dateUtil.getCurrentDayStr();
        parameters.put(OrderConstant.order_class_field_date, currentDay + " - " + currentDay);
        parameters.put(OrderConstant.order_class_field_state, String.valueOf(OrderConstant.order_detail_state_unSale));

        List<Order> orders = orderDao.complexQuery(User.class, parameters, 0, -1);

        long currentTimeMillis = System.currentTimeMillis();
        for (Order order : orders) {
            if ((currentTimeMillis - order.getDate().getTime()) > OrderConstant.order_session_time_millisecond) {

            }
        }
    }*/

    public User getSignUser(String json) {
        Map<String, Object> jsonData = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        User user = (User) orderDao.getFromRedis((String) orderDao.getFromRedis(CommonConstant.sessionId +
                CommonConstant.underline + (String) jsonData.get(CommonConstant.sessionId)));

        User simpleUser = new User();
        simpleUser.setId(user.getId());

        return simpleUser;
    }
}
