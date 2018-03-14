package com.hzg.customer;

import com.google.common.reflect.TypeToken;
import com.hzg.tools.CommonConstant;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customerManagement")
public class CustomerManagementController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(CustomerManagementController.class);

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private Writer writer;

    public CustomerManagementController(CustomerClient customerClient) {
        super(customerClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id,
                           @CookieValue(name= CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"" + CommonConstant.id +"\":" + id + "}";
        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            entities = writer.gson.fromJson(customerClient.unlimitedQuery(entity, json), new TypeToken<List<Customer>>() {}.getType());
        } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            entities = writer.gson.fromJson(customerClient.unlimitedQuery(entity, json), new TypeToken<List<User>>() {}.getType());
        }

        model.put(CommonConstant.entity, entities.isEmpty() ? null : entities.get(0));
        model.put(CommonConstant.sessionId, sessionId);
        logger.info("viewById end");

        return "/customer/" + entity;
    }

    @CrossOrigin
    @RequestMapping(value = "/doBusiness/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public void doBusiness(javax.servlet.http.HttpServletRequest request, HttpServletResponse response,
                           @PathVariable("name") String name, String json, String sessionId) {
        logger.info("doBusiness start, name:" + name + ", json:" + json);
        writer.writeStringToJson(response, customerClient.business(name, json));
        logger.info("doBusiness " + name + " end");
    }
}
