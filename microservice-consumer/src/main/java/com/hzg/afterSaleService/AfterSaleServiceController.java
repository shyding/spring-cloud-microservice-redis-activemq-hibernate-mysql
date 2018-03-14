package com.hzg.afterSaleService;

import com.google.common.reflect.TypeToken;
import com.hzg.order.Order;
import com.hzg.pay.Account;
import com.hzg.pay.PayClient;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/afterSaleService")
public class AfterSaleServiceController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(AfterSaleServiceController.class);

    @Autowired
    private AfterSaleServiceClient afterSaleServiceClient;

    @Autowired
    private PayClient payClient;

    @Autowired
    private Writer writer;

    public AfterSaleServiceController(AfterSaleServiceClient afterSaleServiceClient) {
        super(afterSaleServiceClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"" + CommonConstant.id +"\":" + id + "}";
        if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
            entities = writer.gson.fromJson(afterSaleServiceClient.unlimitedQuery(entity, json), new TypeToken<List<ReturnProduct>>() {}.getType());
        } else if (entity.equalsIgnoreCase(ChangeProduct.class.getSimpleName())) {
            entities = writer.gson.fromJson(afterSaleServiceClient.unlimitedQuery(entity, json), new TypeToken<List<ChangeProduct>>() {}.getType());
        }

        model.put(CommonConstant.resources, dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId) +
                CommonConstant.underline + CommonConstant.resources));
        model.put(CommonConstant.sessionId, sessionId);
        model.put(CommonConstant.entity, entities.isEmpty() ? null : entities.get(0));
        logger.info("viewById end");

        return "/afterSaleService/" + entity;
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("business start, name:" + name + ", json:" + json);

        if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_returnProduct)) {
           model.put(CommonConstant.entity, writer.gson.fromJson(afterSaleServiceClient.business(name, json), ReturnProduct.class));

        } else if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_purchase_returnProduct)) {
           model.put(CommonConstant.entity, writer.gson.fromJson(afterSaleServiceClient.business(name, json), ReturnProduct.class));
           name = AfterSaleServiceConstant.returnProduct;

        } else if (name.equals(AfterSaleServiceConstant.changeProduct_action_name_changeProduct)) {
            model.put(CommonConstant.entity, writer.gson.fromJson(afterSaleServiceClient.business(name, json), ChangeProduct.class));
            model.put("accounts", writer.gson.fromJson(payClient.query(Account.class.getSimpleName().toLowerCase(), "{}"), new com.google.gson.reflect.TypeToken<List<Account>>() {}.getType()));
        }

        model.put(CommonConstant.resources, dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId) +
                CommonConstant.underline + CommonConstant.resources));
        model.put(CommonConstant.sessionId, sessionId);
        logger.info("business " + name + " end");
        return "/afterSaleService/" + name;
    }

    @RequestMapping(value = "/doBusiness/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public void doBusiness(HttpServletResponse response, @PathVariable("name") String name, String json) {
        writer.writeStringToJson(response, afterSaleServiceClient.business(name, json));
    }
}
