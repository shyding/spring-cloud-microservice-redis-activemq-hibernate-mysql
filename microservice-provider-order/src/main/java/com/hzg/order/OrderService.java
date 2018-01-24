package com.hzg.order;

import com.google.gson.reflect.TypeToken;
import com.hzg.afterSaleService.AfterSaleServiceService;
import com.hzg.customer.Express;
import com.hzg.customer.User;
import com.hzg.erp.*;
import com.hzg.pay.Pay;
import com.hzg.pay.Refund;
import com.hzg.sys.Action;
import com.hzg.sys.Company;
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
    private SysClient sysClient;

    @Autowired
    private Writer writer;

    @Autowired
    public ObjectToSql objectToSql;

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private AfterSaleServiceService afterSaleServiceService;

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
            pay.setState(PayConstants.pay_state_apply);
            pay.setBalanceType(PayConstants.balance_type_income);

            pay.setEntity(order.getClass().getSimpleName().toLowerCase());
            pay.setEntityId(order.getId());
            pay.setEntityNo(order.getNo());
            pay.setUser(order.getUser());

            Map<String, String> result1 = writer.gson.fromJson(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);

        } else {
            if (order.getType().compareTo(OrderConstant.order_type_assist) == 0) {
                result += saveBaseOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                result += saveAssistProcessOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                result += savePrivateOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                result += saveOrderBook(order);
            }

            /**
             * 保存前台传递过来的代下单支付记录(支付、收款账号,支付金额等信息)
             */
            for (Pay pay : order.getPays()) {
                pay.setPayDate(dateUtil.getSecondCurrentTimestamp());

                pay.setState(PayConstants.pay_state_apply);
                pay.setBalanceType(PayConstants.balance_type_income);

                pay.setEntity(order.getClass().getSimpleName().toLowerCase());
                pay.setEntityId(order.getId());
                pay.setEntityNo(order.getNo());
                pay.setUser(order.getUser());
            }

            if (order.getType().compareTo(OrderConstant.order_type_book) != 0) {
                for (Pay pay : order.getPays()) {
                    Map<String, String> result1 = writer.gson.fromJson(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                            new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
                    result += result1.get(CommonConstant.result);
                }
            } else {
                Map<String, String> result1 = writer.gson.fromJson(payClient.saveSplitAmountPays(order.getOrderBook().getDeposit(), writer.gson.toJson(order.getPays())),
                        new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
                result += result1.get(CommonConstant.result);
            }

        }

        logger.info("saveOrder end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

    }

    public List<Order> queryOrder(Order order) {
        List<Order> orders = orderDao.query(order);

        for (Order orderItem : orders) {
            for (OrderDetail detail : orderItem.getDetails()) {

                OrderPrivate orderPrivate = new OrderPrivate();
                orderPrivate.setDetail(detail);
                List<OrderPrivate> orderPrivates = orderDao.query(orderPrivate);

                if (!orderPrivates.isEmpty()) {
                    for (OrderPrivate ele : orderPrivates) {
                        if (ele.getAccs() != null) {
                            for (OrderPrivateAcc acc : ele.getAccs()) {
                                OrderPrivateAccProduct accProduct = new OrderPrivateAccProduct();
                                accProduct.setOrderPrivateAcc(acc);
                                List<OrderPrivateAccProduct> accProducts = orderDao.query(accProduct);

                                acc.setProduct(accProducts.get(0).getProduct());
                                acc.setOrderPrivateAccProducts(new HashSet<>(accProducts));
                            }
                        }
                    }

                    detail.setOrderPrivate(orderPrivates.get(0));
                }

                if (detail.getPriceChange() != null) {
                    detail.setPriceChange((ProductPriceChange) orderDao.queryById(detail.getPriceChange().getId(), detail.getPriceChange().getClass()));
                }

                OrderDetailProduct detailProduct = new OrderDetailProduct();
                detailProduct.setOrderDetail(detail);
                List<OrderDetailProduct> detailProducts = orderDao.query(detailProduct);

                detail.setProduct(detailProducts.get(0).getProduct());
                detail.setOrderDetailProducts(new HashSet<>(detailProducts));

                List<Express> expresses = writer.gson.fromJson(customerClient.unlimitedQuery(detail.getExpress().getClass().getSimpleName(),
                        writer.gson.toJson(detail.getExpress())), new TypeToken<List<Express>>(){}.getType());

                detail.setExpress(expresses.isEmpty() ? null : expresses.get(0));
            }

            if (orderItem.getGifts() != null) {
                for (OrderGift gift : orderItem.getGifts()) {
                    OrderGiftProduct giftProduct = new OrderGiftProduct();
                    giftProduct.setOrderGift(gift);
                    List<OrderGiftProduct> giftProducts = orderDao.query(giftProduct);

                    gift.setProduct(giftProducts.get(0).getProduct());
                    gift.setOrderGiftProducts(new HashSet<>(giftProducts));
                }
            }

            if (orderItem.getType().compareTo(OrderConstant.order_type_book) == 0) {
                OrderBook queryOrderBook = new OrderBook();
                queryOrderBook.setOrder(orderItem);
                orderItem.setOrderBook((OrderBook) orderDao.query(queryOrderBook).get(0));
            }

            orderItem.setPays(queryPaysByOrder(orderItem));
        }

        return orders;
    }

    public List<Pay> queryPaysByOrder(Order order) {
        Pay pay = new Pay();
        pay.setEntity(order.getClass().getSimpleName().toLowerCase());
        pay.setEntityId(order.getId());
        return  writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new TypeToken<List<Pay>>() {}.getType());
    }

    public List<Pay> queryDepositPaysByOrderBook(Order order) {
        Pay pay = new Pay();
        pay.setEntity(order.getClass().getSimpleName().toLowerCase());
        pay.setEntityId(order.getId());
        List<Pay> pays = writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new TypeToken<List<Pay>>() {}.getType());

        /**
         * 预订单支付的记录，是按照订金支付记录，余款支付记录先后排列的，所以订金支付记录的 id 是小的。
         * 这里设置支付记录根据 id 由小到大排序，以便找到订金支付记录
         */
        pays.sort(new Comparator<Pay>() {
            @Override
            public int compare(Pay o1, Pay o2) {
                if (o1.getId().compareTo(o2.getId()) > 0) {
                    return 1;
                } else if (o1.getId().compareTo(o2.getId()) < 0) {
                    return -1;
                }

                return 0;
            }
        });

        List<Pay> depositPays = new ArrayList<>();
        Float sumAmount = 0f;
        for (int i = 0; i < pays.size(); i++) {
            sumAmount = new BigDecimal(Float.toString(sumAmount)).add(new BigDecimal(Float.toString(pays.get(i).getAmount()))).floatValue();

            if (sumAmount.compareTo(order.getOrderBook().getDeposit()) <= 0) {
                depositPays.add(pays.get(i));
            } else {
                break;
            }
        }

        return depositPays;
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
            String queryJson = "{\"" + ErpConstant.product + "\":{\"no\":" + detail.getProductNo() + "}";
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
                result += "商品" + detail.getProductNo() + "支付金额不对;";
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

        logger.info("saveBaseOrder start:" + result);

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

            /**
             * 保存订单明细对应商品记录
             */
            Product queryProduct = new Product();
            queryProduct.setNo(detail.getProductNo());
            queryProduct.setState(ErpConstant.product_state_onSale);
            List<Product> products = writer.gson.fromJson(erpClient.query(queryProduct.getClass().getSimpleName(), writer.gson.toJson(queryProduct)),
                    new TypeToken<List<Product>>(){}.getType());

            int productQuantity = detail.getQuantity().intValue();
            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                productQuantity = 1;
            }

            for (int i = 0; i < productQuantity; i++) {
                OrderDetailProduct detailProduct = new OrderDetailProduct();
                detailProduct.setProduct(products.get(i));
                detailProduct.setOrderDetail(detail);

                result += orderDao.save(detailProduct);
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                gift.setOrder(order);
                result += orderDao.save(gift);

                /**
                 * 保存订单赠品对应商品记录
                 */
                Product queryProduct = new Product();
                queryProduct.setNo(gift.getProductNo());
                queryProduct.setState(ErpConstant.product_state_onSale);
                List<Product> products = writer.gson.fromJson(erpClient.query(queryProduct.getClass().getSimpleName(), writer.gson.toJson(queryProduct)),
                        new TypeToken<List<Product>>(){}.getType());

                for (Product product : products) {
                    OrderGiftProduct giftProduct = new OrderGiftProduct();
                    giftProduct.setProduct(product);
                    giftProduct.setOrderGift(gift);

                    result += orderDao.save(giftProduct);
                }

            }
        }

        logger.info("saveBaseOrder end, result:" + result);

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

                /**
                 * 保存私人订制配饰对应商品记录
                 */
                Product queryProduct = new Product();
                queryProduct.setNo(acc.getProductNo());
                queryProduct.setState(ErpConstant.product_state_onSale);
                List<Product> products = writer.gson.fromJson(erpClient.query(queryProduct.getClass().getSimpleName(), writer.gson.toJson(queryProduct)),
                        new TypeToken<List<Product>>(){}.getType());

                for (Product product : products) {
                    OrderPrivateAccProduct accProduct = new OrderPrivateAccProduct();
                    accProduct.setProduct(product);
                    accProduct.setOrderPrivateAcc(acc);

                    result += orderDao.save(accProduct);
                }
            }
        }

        logger.info("savePrivateOrder end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveOrderBook(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveBookService start:" + result);

        result += saveBaseOrder(order);

        Order idOrder = new Order();
        idOrder.setId(order.getId());

        order.getOrderBook().setOrder(idOrder);
        order.getOrderBook().setState(OrderConstant.order_book_state_upPay);
        result += orderDao.save(order.getOrderBook());

        logger.info("saveBookService end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 检查是否可销售
     * @param order
     * @return
     */
    public String isCanSell(Order order) {
        String canSellMsg = "";

        for (OrderDetail detail : order.getDetails()) {
            Float sellableQuantity = getOnSaleQuantity(detail.getProductNo());

            if (sellableQuantity.compareTo(detail.getQuantity()) < 0) {
                canSellMsg += detail.getQuantity() + detail.getUnit() + "编号为:" + detail.getProductNo() +
                        "的商品，但该商品可售数量为：" + sellableQuantity + "；";
            }

            if (detail.getOrderPrivate() != null) {
                if (detail.getOrderPrivate().getAccs() != null) {
                    for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {
                        sellableQuantity = getOnSaleQuantity(acc.getProductNo());

                        if (sellableQuantity.compareTo(acc.getQuantity()) < 0) {
                            canSellMsg += "；配饰:" + acc.getProductNo() + "数量为：" + acc.getQuantity() + acc.getUnit() +
                                    "，但该配饰可售数量为：" + sellableQuantity + "；";
                        }
                    }
                }
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                Float sellableQuantity = getOnSaleQuantity(gift.getProductNo());

                if (sellableQuantity.compareTo(gift.getQuantity()) < 0) {
                    canSellMsg += "；赠品:" + gift.getProductNo() + "数量为：" + gift.getQuantity() + gift.getUnit() +
                    "，但该赠品可售数量为：" + sellableQuantity + "；";
                }
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
        int lockTime = OrderConstant.order_session_time;

        if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
            if (order.getOrderBook().getDeposit().compareTo(order.getAmount()/2) >= 0) {
                lockTime = OrderConstant.order_book_deposit_notLess_half_product_lock_time;
            } else {
                lockTime = OrderConstant.order_book_deposit_less_half_product_lock_time;
            }
        }

        for (OrderDetail detail : order.getDetails()) {
            String key = detail.getProductNo() + CommonConstant.underline + order.getNo();
            orderDao.storeToRedis(key, detail.getQuantity(), lockTime);
            orderDao.putKeyToList(OrderConstant.lock_product_quantity + CommonConstant.underline + detail.getProductNo(), key);

            if (detail.getOrderPrivate() != null) {
                if (detail.getOrderPrivate().getAccs() != null) {
                    for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {

                        key = acc.getProductNo() + CommonConstant.underline + order.getNo();
                        orderDao.storeToRedis(key, acc.getQuantity(), lockTime);
                        orderDao.putKeyToList(OrderConstant.lock_product_quantity + CommonConstant.underline + acc.getProductNo(), key);
                    }
                }
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                String key = gift.getProductNo() + CommonConstant.underline + order.getNo();
                orderDao.storeToRedis(key, gift.getQuantity(), lockTime);
                orderDao.putKeyToList(OrderConstant.lock_product_quantity + CommonConstant.underline + gift.getProductNo(), key);
            }
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
            sellableQuantity = new BigDecimal(Float.toString(sellableQuantity)).subtract(new BigDecimal(Float.toString((Float) lockQuantity))).floatValue();
        }

        return sellableQuantity;
    }

    /**
     * 获取商品已售数量，已经预定的商品不算在已售商品数量里
     * @param product
     * @return
     */
    public Float getProductSoldQuantity(Product product) {
        List<OrderDetail> details = getOrderSoldDetails(product);

        Float soldQuantity = 0f;
        for (OrderDetail detail : details) {
            Float itemQuantity;
            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                itemQuantity = detail.getQuantity();
            } else {
                itemQuantity = 1f;
            }

            soldQuantity = new BigDecimal(Float.toString(soldQuantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
        }

        return new BigDecimal(Float.toString(soldQuantity)).
                subtract(new BigDecimal(Float.toString(afterSaleServiceService.getProductRepeatReturnedQuantity(product)))).floatValue();
    }

    public List<OrderDetail> getOrderSoldDetails(Product product) {
        List<OrderDetail> details = getOrderDetails(product);

        Iterator<OrderDetail> iterator = details.iterator();
        while (iterator.hasNext()) {
            OrderDetail detail = iterator.next();
            if (detail.getState().compareTo(OrderConstant.order_detail_state_sold) == 0) {
                iterator.remove();
            }
        }

        return details;
    }

    public List<OrderDetail> getOrderDetails(Product product) {
        OrderDetailProduct queryDetailProduct = new OrderDetailProduct();
        queryDetailProduct.setProduct(product);
        List<OrderDetailProduct> detailProducts = orderDao.query(queryDetailProduct);

        List<OrderDetail> details = new ArrayList<>();

        for (OrderDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (OrderDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getOrderDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                details.add((OrderDetail)orderDao.queryById(detailProduct.getOrderDetail().getId(), detailProduct.getOrderDetail().getClass()));
            }
        }

        return details;
    }


    /**
     * 取消订单
     * @param order
     */
    public String cancelOrder(Order order) {
        String result;

        for (OrderDetail detail : order.getDetails()) {
            orderDao.deleteFromRedis(detail.getProductNo() + CommonConstant.underline + order.getNo());

            if (detail.getOrderPrivate() != null) {
                if (detail.getOrderPrivate().getAccs() != null) {
                    for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {
                        orderDao.deleteFromRedis(acc.getProductNo() + CommonConstant.underline + order.getNo());
                    }
                }
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                orderDao.deleteFromRedis(gift.getProductNo() + CommonConstant.underline + order.getNo());
            }
        }

        order.setState(OrderConstant.order_state_cancel);
        result = orderDao.updateById(order.getId(), order);

        return result;
    }

    /**
     * 确认线下支付订单已付款
     * @param order
     */
    public String paidOfflineOrder(Order order) {
        String result = paidOrder(order);
        result += payClient.offlinePaid(writer.gson.toJson(order.getPays()));
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 确认线上支付订单已付款
     * @param order
     */
    public String paidOnlineOrder(Order order) {
        order.setPays(queryPaysByOrder(order));
        return paidOrder(order);
    }

    /**
     * 确认预订单订金已付款
     * @param order
     */
    public String paidOrderBook(Order order) {
        String result = CommonConstant.fail;

        order.getOrderBook().setState(OrderConstant.order_book_state_paid);
        result += orderDao.updateById(order.getOrderBook().getId(), order.getOrderBook());
        result += payClient.offlinePaid(writer.gson.toJson(queryDepositPaysByOrderBook(order)));

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 确认订单已付款
     * @param order
     */
    public String paidOrder(Order order) {
        String result = CommonConstant.fail;

        for (OrderDetail detail : order.getDetails()) {
            detail.setState(OrderConstant.order_detail_state_sold);
            result += orderDao.updateById(detail.getId(), detail);
        }

        order.setState(OrderConstant.order_state_paid_confirm);
        order.setSoldDate(dateUtil.getSecondCurrentTimestamp());
        result += orderDao.updateById(order.getId(), order);

        result += setProductsSold(order);
        result += stockOut(order);
        sfExpressOrder(order);

        /**
         * 移除订购时锁定的商品
         */
        for (OrderDetail detail : order.getDetails()) {
            orderDao.deleteFromRedis(detail.getProductNo() + CommonConstant.underline + order.getNo());
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 设置订单退款状态
     * @param order
     * @return
     */
    public String setOrderRefundState(Order order) {
        String result = CommonConstant.fail;

        Pay queryPay = new Pay();
        queryPay.setEntity(order.getClass().getSimpleName().toLowerCase());
        queryPay.setEntityId(order.getId());
        List<Pay> pays = writer.gson.fromJson(payClient.query(queryPay.getClass().getSimpleName(), writer.gson.toJson(queryPay)), new TypeToken<List<Pay>>(){}.getType());

        Float refundAmount = 0f;
        for (Pay pay : pays) {
            List<Refund> refunds = writer.gson.fromJson(payClient.privateQuery(PayConstants.pay_private_query_queryRefundByPay, writer.gson.toJson(pay)), new TypeToken<List<Refund>>(){}.getType());

            for (Refund refund : refunds) {
                refundAmount = new BigDecimal(Float.toString(refundAmount)).add(new BigDecimal(Float.toString(refund.getAmount()))).floatValue();
            }
        }

        Order dbOrder = (Order) orderDao.queryById(order.getId(), order.getClass());

        if (dbOrder.getAmount().compareTo(refundAmount) > 0) {
            dbOrder.setState(OrderConstant.order_state_refund_part);
        } else {
            dbOrder.setState(OrderConstant.order_state_refund);
        }

        result += orderDao.updateById(dbOrder.getId(), dbOrder);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 审核通过自助单
     * @param order
     */
    public String audit(Order order) {
        order.setState(OrderConstant.order_state_paid_confirm);
        return orderDao.updateById(order.getId(), order);
    }

    /**
     * 设置商品为已售状态
     * @param order
     * @return
     */
    private String setProductsSold(Order order) {
        List<Product> products = new ArrayList<>();
        for (OrderDetail detail : order.getDetails()) {
            for (OrderDetailProduct detailProduct : detail.getOrderDetailProducts()) {
                /**
                 * size > 1 表示商品是按件数卖，<= 1 表示商品是按件数或者重量或者其他不可数单位卖
                 */
                detailProduct.getProduct().setSoldQuantity(detail.getOrderDetailProducts().size() > 1 ? 1 : detail.getQuantity());
                detailProduct.getProduct().setSoldUnit(detail.getUnit());
                products.add(detailProduct.getProduct());
            }

            if (detail.getOrderPrivate() != null) {
                if (detail.getOrderPrivate().getAccs() != null) {
                    for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {
                        for (OrderPrivateAccProduct accProduct : acc.getOrderPrivateAccProducts()) {
                            accProduct.getProduct().setSoldQuantity(acc.getOrderPrivateAccProducts().size() > 1 ? 1 : acc.getQuantity());
                            accProduct.getProduct().setSoldUnit(acc.getUnit());
                            products.add(accProduct.getProduct());
                        }
                    }
                }
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                for (OrderGiftProduct giftProduct : gift.getOrderGiftProducts()) {
                    giftProduct.getProduct().setSoldQuantity(gift.getOrderGiftProducts().size() > 1 ? 1 : gift.getQuantity());
                    giftProduct.getProduct().setSoldUnit(gift.getUnit());
                    products.add(giftProduct.getProduct());
                }
            }
        }

        return erpClient.business(ErpConstant.product_action_name_setProductsSold, writer.gson.toJson(products));
    }


    private String stockOut(Order order) {
        String result = CommonConstant.fail;

        Map<String, Object> saveResult = writer.gson.fromJson(saveStockOut(order), new TypeToken<Map<String, Object>>(){}.getType());
        result += (String) saveResult.get(CommonConstant.result);

        if (saveResult.get(CommonConstant.result).equals(CommonConstant.success)) {
            Action action = new Action();
            String entityIdStr = String.valueOf(saveResult.get(CommonConstant.id));
            action.setEntityId(Integer.parseInt(entityIdStr.substring(0, entityIdStr.indexOf("."))));
            action.setSessionId(order.getSessionId());
            result += erpClient.business(ErpConstant.stockInOut_action_name_outProduct, writer.gson.toJson(action));
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 插入出库数据
     */
    private String saveStockOut(Order order) {
        StockInOut stockOut = new StockInOut();
        stockOut.setNo(((Map<String, String>)writer.gson.fromJson(erpClient.getNo(ErpConstant.no_stockInOut_perfix), new TypeToken<Map<String, String>>() {}.getType())).get(CommonConstant.no));
        stockOut.setType(ErpConstant.stockInOut_type_normal_outWarehouse);

        stockOut.setState(ErpConstant.stockInOut_state_finished);
        stockOut.setDate(dateUtil.getSecondCurrentTimestamp());
        stockOut.setInputDate(dateUtil.getSecondCurrentTimestamp());
        stockOut.setDescribes("销售订单：" + order.getNo() + "支付完成，货品自动出库");
        stockOut.setWarehouse(getWarehouseByUser(order.getSaler()));

        Set<StockInOutDetail> stockOutDetails = new HashSet<>();
        for (OrderDetail detail : order.getDetails()) {
            StockInOutDetail stockOutDetail = new StockInOutDetail();
            stockOutDetail.setProductNo(detail.getProductNo());
            stockOutDetail.setQuantity(detail.getQuantity());
            stockOutDetail.setUnit(detail.getUnit());

            Set<StockInOutDetailProduct> detailProducts = new HashSet<>();
            for (OrderDetailProduct orderDetailProduct : detail.getOrderDetailProducts()) {
                StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
                detailProduct.setProduct(orderDetailProduct.getProduct());
                detailProducts.add(detailProduct);
            }
            stockOutDetail.setStockInOutDetailProducts(detailProducts);

            stockOutDetails.add(stockOutDetail);


            if (detail.getOrderPrivate() != null) {
                if (detail.getOrderPrivate().getAccs() != null) {
                    for (OrderPrivateAcc acc : detail.getOrderPrivate().getAccs()) {
                        StockInOutDetail accStockOutDetail = new StockInOutDetail();
                        accStockOutDetail.setQuantity(acc.getQuantity());
                        accStockOutDetail.setUnit(acc.getUnit());

                        Set<StockInOutDetailProduct> accDetailProducts = new HashSet<>();
                        for (OrderPrivateAccProduct accProduct : acc.getOrderPrivateAccProducts()) {
                            StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
                            detailProduct.setProduct(accProduct.getProduct());
                            accDetailProducts.add(detailProduct);
                        }
                        accStockOutDetail.setStockInOutDetailProducts(accDetailProducts);

                        stockOutDetails.add(accStockOutDetail);
                    }
                }
            }
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                StockInOutDetail stockOutDetail = new StockInOutDetail();
                stockOutDetail.setQuantity(gift.getQuantity());
                stockOutDetail.setUnit(gift.getUnit());

                Set<StockInOutDetailProduct> detailProducts = new HashSet<>();
                for (OrderGiftProduct giftProduct : gift.getOrderGiftProducts()) {
                    StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
                    detailProduct.setProduct(giftProduct.getProduct());
                    detailProducts.add(detailProduct);
                }
                stockOutDetail.setStockInOutDetailProducts(detailProducts);

                stockOutDetails.add(stockOutDetail);
            }
        }

        stockOut.setDetails(stockOutDetails);
        return  erpClient.save(stockOut.getClass().getSimpleName(), writer.gson.toJson(stockOut));
    }

    private String sfExpressOrder(Order order) {
        com.hzg.customer.User user = ((List<com.hzg.customer.User>)writer.gson.fromJson(
                customerClient.unlimitedQuery(order.getUser().getClass().getSimpleName(), writer.gson.toJson(order.getUser())), new TypeToken<List<User>>(){}.getType())).get(0);

        OrderDetail expressDetail = (OrderDetail) order.getDetails().toArray()[0];
        Express receiver = ((List<Express>)writer.gson.fromJson(
                customerClient.unlimitedQuery(expressDetail.getExpress().getClass().getSimpleName(), writer.gson.toJson(expressDetail.getExpress())),
                new TypeToken<List<Express>>(){}.getType())).get(0);

        StockInOut stockOut = writer.gson.fromJson(erpClient.getLastStockInOutByProductAndType(
                writer.gson.toJson(((OrderDetailProduct)expressDetail.getOrderDetailProducts().toArray()[0]).getProduct()), ErpConstant.stockOut),
                StockInOut.class);
        Company senderInfo = ((List<Warehouse>)writer.gson.fromJson(
                erpClient.query(stockOut.getWarehouse().getClass().getSimpleName(), writer.gson.toJson(stockOut.getWarehouse())),
                new TypeToken<List<Warehouse>>(){}.getType())).get(0).getCompany();
        com.hzg.sys.User sender = stockOut.getInputer();

        ExpressDeliver expressDeliver = new ExpressDeliver();
        expressDeliver.setDeliver(ErpConstant.deliver_sfExpress);
        expressDeliver.setType(ErpConstant.deliver_sfExpress_type);
        expressDeliver.setDate(expressDetail.getExpressDate());

        expressDeliver.setReceiver(receiver.getReceiver());
        expressDeliver.setReceiverAddress(receiver.getAddress());
        expressDeliver.setReceiverCity(receiver.getCity());
        expressDeliver.setReceiverProvince(receiver.getProvince());
        expressDeliver.setReceiverCountry(receiver.getCountry());
        expressDeliver.setReceiverCompany(user.getCustomer().getHirer());
        expressDeliver.setReceiverMobile(receiver.getMobile());
        expressDeliver.setReceiverTel(receiver.getPhone());
        expressDeliver.setReceiverPostCode(receiver.getPostCode());

        expressDeliver.setSender(sender.getName());
        expressDeliver.setSenderAddress(senderInfo.getAddress());
        expressDeliver.setSenderCity(senderInfo.getCity());
        expressDeliver.setSenderProvince(senderInfo.getProvince());
        expressDeliver.setSenderCountry(senderInfo.getCountry());
        expressDeliver.setSenderCompany(senderInfo.getName());
        expressDeliver.setSenderMobile(sender.getMobile());
        expressDeliver.setSenderTel(senderInfo.getPhone());
        expressDeliver.setSenderPostCode(senderInfo.getPostCode());

        Set<ExpressDeliverDetail> deliverDetails = new HashSet<>();

        for (OrderDetail orderDetail : order.getDetails()) {
            ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
            expressDeliverDetail.setProductNo(orderDetail.getProductNo());
            expressDeliverDetail.setQuantity(orderDetail.getQuantity());
            expressDeliverDetail.setUnit(orderDetail.getUnit());
            expressDeliverDetail.setPrice(orderDetail.getProductPrice());

            Set<ExpressDeliverDetailProduct> deliverDetailProducts = new HashSet<>();
            for (OrderDetailProduct orderDetailProduct : orderDetail.getOrderDetailProducts()) {
                ExpressDeliverDetailProduct deliverDetailProduct = new ExpressDeliverDetailProduct();
                deliverDetailProduct.setProduct(orderDetailProduct.getProduct());
                deliverDetailProducts.add(deliverDetailProduct);
            }

            expressDeliverDetail.setExpressDeliverDetailProducts(deliverDetailProducts);
            deliverDetails.add(expressDeliverDetail);
        }

        if (order.getGifts() != null) {
            for (OrderGift gift : order.getGifts()) {
                ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
                expressDeliverDetail.setProductNo(gift.getProductNo());
                expressDeliverDetail.setQuantity(gift.getQuantity());
                expressDeliverDetail.setUnit(gift.getUnit());

                Set<ExpressDeliverDetailProduct> deliverDetailProducts = new HashSet<>();
                for (OrderGiftProduct giftProduct : gift.getOrderGiftProducts()) {
                    ExpressDeliverDetailProduct deliverDetailProduct = new ExpressDeliverDetailProduct();
                    deliverDetailProduct.setProduct(giftProduct.getProduct());
                    deliverDetailProducts.add(deliverDetailProduct);
                }

                expressDeliverDetail.setExpressDeliverDetailProducts(deliverDetailProducts);
                deliverDetails.add(expressDeliverDetail);
            }
        }

        expressDeliver.setDetails(deliverDetails);

        List<ExpressDeliver> expressDelivers = new ArrayList<>();
        expressDelivers.add(expressDeliver);
        return erpClient.sfExpressOrder(writer.gson.toJson(expressDelivers));
    }

    public Warehouse getWarehouseByUser(com.hzg.sys.User user) {
        return writer.gson.fromJson(erpClient.getWarehouseByCompany(sysClient.getCompanyByUser(writer.gson.toJson(user))), Warehouse.class);
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

            fromSql += ", " + objectToSql.getTableName(OrderDetailProduct.class) + " t21 ";
            whereSql += " and t21." + objectToSql.getColumn(OrderDetailProduct.class.getDeclaredField("orderDetail")) +
                    " = t2." + objectToSql.getColumn(OrderDetail.class.getDeclaredField("id"));

            selectSql += ", " + orderDao.getSelectColumns("t211", Product.class);
            fromSql += ", " + objectToSql.getTableName(Product.class) + " t211 ";
            whereSql += " and t211." + objectToSql.getColumn(Product.class.getDeclaredField("id")) +
                    " = t21." + objectToSql.getColumn(OrderDetailProduct.class.getDeclaredField("product"));

            selectSql += ", " + orderDao.getSelectColumns("t3", OrderPrivate.class);
            fromSql += ", " + objectToSql.getTableName(OrderPrivate.class) + " t3 ";
            whereSql += " and t3." + objectToSql.getColumn(OrderPrivate.class.getDeclaredField("detail")) +
                    " = t2." + objectToSql.getColumn(OrderDetail.class.getDeclaredField("id"));

            sql = "select distinct " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }
}
