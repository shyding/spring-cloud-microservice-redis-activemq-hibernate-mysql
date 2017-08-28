package com.hzg.order;

import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/order")
public class OrderController extends com.hzg.base.SessionController {

    Logger logger = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private Writer writer;

    @Autowired
    private StrUtil strUtil;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue orderQueue;

    public OrderController(OrderClient orderClient) {
        super(orderClient);
    }

    @PostMapping("/save")
    public void save(HttpServletResponse response, String json,
                     @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("save order start:" + json);

        String orderSessionId = strUtil.generateDateRandomStr(32);
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.orderSessionId + "\":\"" + orderSessionId + "\"," +
                "\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";

        jmsMessagingTemplate.convertAndSend(orderQueue, json);
        writer.writeStringToJsonAccessAllow(response, "{\"" + CommonConstant.orderSessionId + "\":\"" + orderSessionId + "\"}");

        logger.info("save order end");
    }

    @GetMapping("/saveResult/{" + CommonConstant.orderSessionId +"}")
    public void saveResult(HttpServletResponse response, @PathVariable(CommonConstant.orderSessionId) String orderSessionId) {
        logger.info("saveResult start:" + orderSessionId);
        writer.writeStringToJsonAccessAllow(response, orderClient.saveResult(orderSessionId));
        logger.info("saveResult end");
    }

    @PostMapping("/cancel")
    public void cancel(HttpServletResponse response, String json,
                       @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("cancel order start:" + json);

        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeObjectToJsonAccessAllow(response, orderClient.cancel(json));

        logger.info("cancel order end");
    }


}