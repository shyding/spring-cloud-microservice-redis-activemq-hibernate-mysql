package com.hzg.order;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.*;
import com.hzg.sys.*;
import com.hzg.sys.User;
import com.hzg.tools.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
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

    /**
     * 使用信息队列保存用户订单
     * 订单消息队列里一有消息，就会把消息自动发送至该方法，该方法然后保存订单信息
     * @param json
     */
    @Transactional
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
    @Transactional
    @PostMapping("/save")
    public void save(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("save start, parameter:" + entity + ":" + json);
        String result = CommonConstant.fail;

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            Order order = writer.gson.fromJson(json, Order.class);
            result = saveOrder(order);

            writer.writeStringToJson(response, result) ;
            logger.info("save end, result:" + result);

            return;
        }

        logger.info("save end, result:" + result);
    }

    /**
     * 保存订单，成功则返回订单号，失败则返回失败消息
     * @param order
     * @return
     */
    public String saveOrder(Order order) {
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
            saveInfo =  "{\"" + OrderConstant.order_no + "\":\"" + order.getNo() + "\"}";

        } else {
            saveInfo = "{\"" + CommonConstant.result + "\":\"" + result + "\"}";
        }

        return saveInfo;
    }

    @Transactional
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        com.hzg.customer.User signUser = getSignUser(json);

        try {
            if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
                Order order = writer.gson.fromJson(json, Order.class);
                Order dbOrder = (Order) orderDao.queryById(order.getId(), order.getClass());

                if (dbOrder.getUser().getId().compareTo(signUser.getId()) == 0) {
                    result += orderDao.updateById(order.getId(), order);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("update end, result:" + result);
    }

    @Transactional
    @PostMapping("/cancel")
    public void cancel(HttpServletResponse response, String json){
        logger.info("cancel start, parameter:" + json);

        String result = CommonConstant.fail;

        com.hzg.customer.User signUser = getSignUser(json);

        try {
            Order order = writer.gson.fromJson(json, Order.class);
            Order dbOrder = (Order) orderDao.query(order).get(0);

            if (dbOrder.getUser().getId().compareTo(signUser.getId()) == 0) {
                order.setState(OrderConstant.order_state_cancel);
                result += orderService.cancelOrder(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("cancel end, result:" + result);
    }

    public com.hzg.customer.User getSignUser(String json) {
        Map<String, Object> jsonData = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        return (com.hzg.customer.User) orderDao.getFromRedis((String) orderDao.getFromRedis(CommonConstant.sessionId +
                CommonConstant.underline + (String) jsonData.get(CommonConstant.sessionId)));
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.query(writer.gson.fromJson(json, Order.class)));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.suggest(writer.gson.fromJson(json, Order.class), null));
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            writer.writeObjectToJson(response, orderDao.complexQuery(Order.class, queryParameters, position, rowNum));
        }

        logger.info("complexQuery end");
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

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            recordsSum = orderDao.recordsSum(Order.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }
}
