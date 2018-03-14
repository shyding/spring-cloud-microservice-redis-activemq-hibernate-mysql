package com.hzg.customer;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController extends com.hzg.base.SessionController {

    Logger logger = Logger.getLogger(CustomerController.class);

    @Autowired
    private Writer writer;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private SmsClient smsClient;

    @Autowired
    private StrUtil strUtil;

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    public Integer sessionTime;

    public CustomerController(CustomerClient customerClient) {
        super(customerClient);
    }

    @CrossOrigin
    @RequestMapping(value = "/user/modifyPassword", method = {RequestMethod.GET, RequestMethod.POST})
    public void modifyPassword(HttpServletResponse response, String json) {
        logger.info("modifyPassword start, json:" + json);
        writer.writeStringToJson(response, customerClient.business(CustomerConstant.user_action_name_modifyPassword, json));
        logger.info("modifyPassword end");
    }

    /**
     * 到登录页面，设置加密密码的 salt
     * @param response
     * @param model
     * @return
     */
    @CrossOrigin
    @GetMapping("/user/signIn")
    public void signIn(HttpServletResponse response, Map<String, Object> model) {
        String sessionId = strUtil.generateDateRandomStr(32);

        String salt = "";
        if (model.get(CommonConstant.oldSessionId) == null) {
            salt = strUtil.generateRandomStr(256);
        } else {
            salt = (String) dao.getFromRedis(CommonConstant.salt + CommonConstant.underline + (String)model.get(CommonConstant.oldSessionId));
        }

        cookieUtils.addCookie(response, CommonConstant.sessionId, sessionId);
        dao.storeToRedis(CommonConstant.salt + CommonConstant.underline + sessionId, salt, sessionTime);

        model.put(CommonConstant.salt, salt);
        model.put(CommonConstant.sessionId, sessionId);

        writer.writeObjectToJson(response, model);
    }

    /**
     * 用户登录，注销，重复登录
     * @param name
     * @param json
     * @return
     */
    @CrossOrigin
    @PostMapping("/user/{name}")
    public void user(HttpServletRequest request, HttpServletResponse response, @PathVariable("name") String name, String json) {
        logger.info("user start, name:" + name + ", json:" + json);

        Map<String, String> result = null;
        if (name.equals("signIn")) {
            result = writer.gson.fromJson(customerClient.signIn(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("signOut")) {
            cookieUtils.delCookie(request, response, CommonConstant.sessionId);
            result = writer.gson.fromJson(customerClient.signOut(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("hasLoginDeal")) {
            result = writer.gson.fromJson(customerClient.hasLoginDeal(json), new TypeToken<Map<String, String>>(){}.getType());
        }

        if (result.get(CommonConstant.result).equals(CommonConstant.success)) {
            writer.writeObjectToJson(response, result);

        } else {
            Map<String, String> formInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

            Map<String, Object> model = new HashedMap();
            model.put(CommonConstant.result, result.get(CommonConstant.result));
            model.put(CommonConstant.oldSessionId, formInfo.get(CommonConstant.sessionId));

            signIn(response, model);
        }

        logger.info("user " + name + " end");
    }

    @CrossOrigin
    @GetMapping("/user/getValidateCode/{mobileNumber}")
    public void getValidateCode(HttpServletResponse response, @PathVariable("mobileNumber") String mobileNumber) {
        String validateCodeStr = smsClient.generateValidateCode(SmsConstant.validateCodeLength, mobileNumber);
        Map<String, String> validateCode = writer.gson.fromJson(validateCodeStr, new TypeToken<Map<String, String>>(){}.getType());

        if (!validateCodeStr.contains(CommonConstant.fail)) {
            writer.writeStringToJson(response, smsClient.send("用户注册验证码是：" + validateCode.get(SmsConstant.validateCode) + ",请查收"));
        } else {
            writer.writeStringToJson(response, validateCodeStr);
        }
    }
}
