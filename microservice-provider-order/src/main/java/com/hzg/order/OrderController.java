package com.hzg.order;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.Express;
import com.hzg.customer.User;
import com.hzg.erp.Product;
import com.hzg.erp.ProductPriceChange;
import com.hzg.pay.Pay;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    Logger logger = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private Writer writer;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    CustomerClient customerClient;

    /**
     * 使用消息队列保存用户订单
     * 订单消息队列里一有消息，就会把消息自动发送至该方法，该方法然后保存订单信息
     * @param json
     */
    @JmsListener(destination = OrderConstant.queue_order)
    public void saveQueueOrder(String json) {
        logger.info("saveQueueOrder start, parameter:" + json);

        Order order = writer.gson.fromJson(json, Order.class);
        String result = saveOrder(order);
        orderDao.storeToRedis(order.getOrderSessionId(), result, OrderConstant.order_session_time);

        logger.info("saveQueueOrder end, result:" + result);
    }

    /**
     * 保存实体
     * @param response
     * @param entity
     * @param json
     */
    @PostMapping("/save")
    public void save(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("save start, parameter:" + entity + ":" + json);
        String result = CommonConstant.fail;

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            Order order = writer.gson.fromJson(json, Order.class);
            result = saveOrder(order);

            writer.writeStringToJson(response, result) ;
        }

        logger.info("save end, result:" + result);
    }

    /**
     * 保存订单，成功则返回订单号，失败则返回失败消息
     * @param order
     * @return
     */
    @Transactional
    private String saveOrder(Order order) {
        String result = CommonConstant.fail;

        try {
            result += orderService.saveOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        String saveInfo;
        if (result.equals(CommonConstant.success)) {
            saveInfo =  "{\"" + CommonConstant.result + "\":\"" + CommonConstant.success + "\",\"" + OrderConstant.order_no + "\":\"" + order.getNo() + "\"}";

        } else {
            saveInfo = "{\"" + CommonConstant.result + "\":\"" + result + "\"}";
        }

        return saveInfo;
    }

    @PostMapping("/cancel")
    public void cancel(HttpServletResponse response, @RequestBody String json){
        logger.info("cancel start, parameter:" + json);
        orderOperation(response, json, OrderConstant.order_operate_type_cancel);
        logger.info("cancel end");
    }

    @PostMapping("/paid")
    public void paid(HttpServletResponse response, @RequestBody String json){
        logger.info("paid start, parameter:" + json);
        orderOperation(response, json, OrderConstant.order_operate_type_paid);
        logger.info("paid end");
    }

    @PostMapping("/audit")
    public void audit(HttpServletResponse response, @RequestBody String json){
        logger.info("audit start, parameter:" + json);
        orderOperation(response, json, OrderConstant.order_operate_type_audit);
        logger.info("audit end");
    }

    @Transactional
    private void orderOperation(HttpServletResponse response, String json, String type){
        logger.info("orderOperation start, parameter:" + json + "," + type);

        String result = CommonConstant.fail;

        try {
            Order order = writer.gson.fromJson(json, Order.class);
            Order dbOrder = orderService.queryOrder(new Order(order.getId())).get(0);

            if (dbOrder.getState().compareTo(OrderConstant.order_detail_state_unSale) == 0) {
                if (type.equals(OrderConstant.order_operate_type_cancel)) {
                    result += orderService.cancelOrder(dbOrder);
                }

                if (type.equals(OrderConstant.order_operate_type_paid)) {
                    dbOrder.setPays(order.getPays());
                    result += orderService.paidOrder(dbOrder);
                }

                if (type.equals(OrderConstant.order_operate_type_audit)) {
                    dbOrder.setPays(order.getPays());
                    result += orderService.audit(dbOrder);
                }

                OrderOperation operation = new OrderOperation();
                operation.setOrder(order);
                operation.setType(type);
                operation.setDate(dateUtil.getSecondCurrentTimestamp());
                operation.setUser((com.hzg.sys.User) orderDao.getFromRedis((String)orderDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + order.getSessionId())));

                orderDao.save(operation);

            } else {
                result +=  CommonConstant.fail + ",未支付订单才可以取消或确认收款";
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("orderOperation end, result:" + result);
    }


    @Transactional
    @PostMapping("/business")
    public void business(HttpServletResponse response, String name, @RequestBody String json){
        logger.info("business start, parameter:" + name + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (name.equals("authorizeOrderPrivateAmount")) {
                OrderPrivate orderPrivate = writer.gson.fromJson(json, OrderPrivate.class);

                com.hzg.sys.User user = (com.hzg.sys.User)orderDao.getFromRedis(
                        (String) orderDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + orderPrivate.getAuthorize().getSessionId()));

                if (user != null) {
                    orderPrivate.getAuthorize().setUser(user);
                    orderPrivate.getAuthorize().setDate(dateUtil.getSecondCurrentTimestamp());

                    OrderPrivate dbOrderPrivate = (OrderPrivate) orderDao.queryById(orderPrivate.getId(), orderPrivate.getClass());
                    OrderDetail orderDetail = (OrderDetail)orderDao.queryById(orderPrivate.getDetail().getId(), orderPrivate.getDetail().getClass());
                    Order order = orderDetail.getOrder();

                    if (orderDetail.getState().compareTo(OrderConstant.order_detail_state_unSale) == 0) {
                        if (dbOrderPrivate.getAuthorize() == null) {

                            order.setAmount(new BigDecimal(Float.toString(order.getAmount())).add(new BigDecimal(Float.toString(orderPrivate.getAuthorize().getAmount()))).floatValue());
                            order.setPayAmount(new BigDecimal(Float.toString(order.getPayAmount())).add(new BigDecimal(Float.toString(orderPrivate.getAuthorize().getAmount()))).floatValue());
                        } else {

                            order.setAmount(new BigDecimal(Float.toString(order.getAmount())).
                                    subtract(new BigDecimal(Float.toString(dbOrderPrivate.getAuthorize().getAmount()))).add(new BigDecimal(Float.toString(orderPrivate.getAuthorize().getAmount()))).floatValue());
                            order.setPayAmount(new BigDecimal(Float.toString(order.getPayAmount())).
                                    subtract(new BigDecimal(Float.toString(dbOrderPrivate.getAuthorize().getAmount()))).add(new BigDecimal(Float.toString(orderPrivate.getAuthorize().getAmount()))).floatValue());
                        }

                        result += orderDao.save(orderPrivate.getAuthorize());
                        result += orderDao.updateById(orderPrivate.getId(), orderPrivate);
                        result += orderDao.updateById(order.getId(), order);

                    } else {
                        result += CommonConstant.fail + ",已售商品不能核定金额，核定金额失败";
                    }
                } else {
                    result += CommonConstant.fail + ",查询不到核定金额的用户，核定金额失败";
                }

            } else if (name.equals("paidOrder")) {
                result += orderService.paidOrder(orderService.queryOrder(writer.gson.fromJson(json, Order.class)).get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("business end, result:" + result);
    }

    @GetMapping("/querySaveResult")
    public void querySaveResult(HttpServletResponse response, String orderSessionId){
        logger.info("querySaveResult start, parameter:" + orderSessionId );
        writer.writeStringToJson(response, (String) orderDao.getFromRedis(orderSessionId));
        logger.info("querySaveResult end");
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        Order order = writer.gson.fromJson(json, Order.class);
        order.setUser(orderService.getSignUser(json));
        List<Order> orders = orderService.queryOrder(order);

        writer.writeObjectToJson(response, orders);
        logger.info("query end");
    }

    @RequestMapping(value = "/unlimitedQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedQuery(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedQuery start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            List<Order> orders = orderService.queryOrder(writer.gson.fromJson(json, Order.class));
            writer.writeObjectToJson(response, orders);

        } else if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            List<OrderPrivate> orderPrivates = orderDao.query(writer.gson.fromJson(json, OrderPrivate.class));

            for (OrderPrivate orderPrivate : orderPrivates) {
                OrderDetailProduct detailProduct = new OrderDetailProduct();
                detailProduct.setOrderDetail(orderPrivate.getDetail());
                orderPrivate.getDetail().setProduct(((OrderDetailProduct) orderDao.query(detailProduct).get(0)).getProduct());

                if (orderPrivate.getAccs() != null) {
                    for (OrderPrivateAcc acc : orderPrivate.getAccs()) {
                        OrderPrivateAccProduct accProduct = new OrderPrivateAccProduct();
                        accProduct.setOrderPrivateAcc(acc);
                        List<OrderPrivateAccProduct> accProducts = orderDao.query(accProduct);

                        acc.setProduct(accProducts.get(0).getProduct());
                        acc.setOrderPrivateAccProducts(new HashSet<>(accProducts));
                    }
                }
            }

            writer.writeObjectToJson(response, orderPrivates);

        } else if (entity.equalsIgnoreCase(OrderDetail.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.query(writer.gson.fromJson(json, OrderDetail.class)));
        }

        logger.info("unlimitedQuery end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        Order order = writer.gson.fromJson(json, Order.class);
        order.setUser(orderService.getSignUser(json));

        Field[] limitFields = new Field[1];
        try {
            limitFields[0] = order.getClass().getDeclaredField("user");
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.writeObjectToJson(response, orderDao.suggest(order, limitFields));

        logger.info("suggest end");
    }

    @RequestMapping(value = "/unlimitedSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedSuggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedSuggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.suggest(writer.gson.fromJson(json, Order.class), null));

        } else if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.suggest(writer.gson.fromJson(json, OrderPrivate.class), null));
        }

        logger.info("unlimitedSuggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        User signUser = orderService.getSignUser(json);
        queryParameters.put("user", writer.gson.toJson(signUser));

        writer.writeObjectToJson(response, orderDao.complexQuery(Order.class, queryParameters, position, rowNum));

        logger.info("complexQuery end");
    }

    @RequestMapping(value = "/unlimitedComplexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedComplexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("unlimitedComplexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.complexQuery(Order.class,
                    writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), position, rowNum));

        } else if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderService.privateQuery(entity, json, position, rowNum));
        }

        logger.info("unlimitedComplexQuery end");
    }

    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
    @RequestMapping(value = "/recordsSum", method = {RequestMethod.GET, RequestMethod.POST})
    public void recordsSum(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("recordsSum start, parameter:" + entity + ":" + json);
        BigInteger recordsSum = new BigInteger("-1");

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        User signUser = orderService.getSignUser(json);
        queryParameters.put("user", writer.gson.toJson(signUser));

        recordsSum = orderDao.recordsSum(Order.class, queryParameters);
        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }

    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
    @RequestMapping(value = "/unlimitedRecordsSum", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedRecordsSum(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedRecordsSum start, parameter:" + entity + ":" + json);
        BigInteger recordsSum = new BigInteger("-1");

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            recordsSum = orderDao.recordsSum(Order.class, writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()));

        } else if (entity.equalsIgnoreCase(OrderPrivate.class.getSimpleName())) {
            recordsSum = orderService.privateRecordNum(entity, json);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("unlimitedRecordsSum end");
    }
}
