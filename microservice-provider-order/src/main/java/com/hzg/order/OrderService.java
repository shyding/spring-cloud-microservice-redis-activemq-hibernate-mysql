package com.hzg.order;

import com.hzg.customer.User;
import com.hzg.erp.Product;
import com.hzg.pay.Pay;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

        String canSellMsg = isCanSell(order);
        if (!canSellMsg.equals("")) {
            result += CommonConstant.fail + canSellMsg;

        } else {
            order.setNo(orderDao.getNo(OrderConstant.no_order_perfix));

            result += lockOrderProduct(order);

            order.setState(OrderConstant.order_state_normal);
            order.setDate(dateUtil.getSecondCurrentTimestamp());

            order.setUser((User) orderDao.getFromRedis((String) orderDao.getFromRedis(
                    CommonConstant.sessionId + CommonConstant.underline + order.getSessionId())));


            if (order.getType().compareTo(OrderConstant.order_type_selfService) == 0 ||
                    order.getType().compareTo(OrderConstant.order_type_assist) == 0) {
                result += saveBaseOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                result += saveAssistProcessOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                result += savePrivateOrder(order);

            } else if (order.getType().compareTo(OrderConstant.order_type_book) == 0) {
                result += saveBookOrder(order);
            }

            result += saveOrderPay(order);
        }

        logger.info("saveOrder end, result:" + result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveBaseOrder(Order order) {
        String result = CommonConstant.fail;

        logger.info("saveSelfServiceOrder start:" + result);

        result += orderDao.save(order);

        Order idOrder = new Order();
        idOrder.setId(order.getId());

        for (OrderDetail detail : order.getDetails()) {
            detail.setOrder(idOrder);
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

            if (sellableQuantity.compareTo(detail.getAmount()) <= 0) {
                canSellMsg += detail.getAmount() + detail.getUnit() + "编号为:" + detail.getProduct().getNo() +
                        "的商品，但该商品可售数量为：" + sellableQuantity + ";";
            }
        }

        if (!canSellMsg.equals("")) {
            canSellMsg += "尊敬的顾客你好，你预定了:" + canSellMsg + "预定失败。如有帮助需要，请联系我公司客服人员处理";
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

            orderDao.storeToRedis(detail.getProduct().getNo() + CommonConstant.underline + order.getNo(),
                    detail.getAmount(), lockTime);
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
        List<Object> lockQuantities = orderDao.getValues(productNo + CommonConstant.asterisk);

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
}
