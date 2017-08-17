package com.hzg.sms;

import com.google.gson.reflect.TypeToken;
import com.hzg.base.Dao;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/sms")
public class SmsController {

    Logger logger = Logger.getLogger(SmsController.class);

    @Autowired
    private Dao dao;

    @Autowired
    private Writer writer;

    @Autowired
    private StrUtil strUtil;

    @Autowired
    public DateUtil dateUtil;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public void send(HttpServletResponse response, @RequestBody String json){
        logger.info("send start, parameter:" + json);
        String result = CommonConstant.fail;

        try {
            Map<String, String> smsData = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
            String mobileNumber = smsData.get(SmsConstant.mobileNumber);

            int count = dao.getSendCount(SmsConstant.count + CommonConstant.underline + mobileNumber);
            if (count < SmsConstant.maxSendCountPerDay) {

                long expire = dao.getExpire(mobileNumber);

                if (expire == 0) {
                    result = send(mobileNumber, smsData.get(SmsConstant.content));

                    dao.storeToRedis(mobileNumber, "", SmsConstant.sendInterval);
                    dao.storeToRedisAtDate(SmsConstant.count + CommonConstant.underline + mobileNumber, ++count, dateUtil.getDay(1));

                } else {
                    result += CommonConstant.fail + "," + expire + "秒后，可以再次发送短信到号码为：" + mobileNumber + "的手机";
                }

            } else {
                result += CommonConstant.fail + ",对不起，单日单个手机号最多可以发送" + SmsConstant.maxSendCountPerDay +
                        "条短信。如有帮助需要，请联系我公司客服人员处理";
            }


        } catch (Exception e) {
            e.printStackTrace();
            result = CommonConstant.fail;
        }

        writer.writeObjectToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("send end");
    }

    @RequestMapping(value = "/generateValidateCode", method = RequestMethod.GET)
    public void generateValidateCode(HttpServletResponse response, Integer length, String mobileNumber){
        logger.info("generateValidateCode start, parameter:" + length + "," + mobileNumber);

        String validateCodeKey = strUtil.generateRandomStr(32);
        String validateCode = strUtil.generateRandomNumberStr(length);
        dao.storeToRedis(mobileNumber + CommonConstant.underline + validateCodeKey, validateCode, SmsConstant.validTime);

        writer.writeObjectToJson(response, "{\"" + SmsConstant.validateCode + "\":\"" + validateCode + "\",\"" + SmsConstant.validateCodeKey + "\":\"" + validateCodeKey + "\"}");
        logger.info("generateValidateCode end");
    }

    @RequestMapping(value = "/getValidateCode", method = RequestMethod.GET)
    public void getValidateCode(HttpServletResponse response, String mobileNumber, String validateCodeKey){
        logger.info("getValidateCode start, parameter:" + mobileNumber + "," + validateCodeKey);

        writer.writeObjectToJson(response, "{\"" + SmsConstant.validateCode + "\":\"" + dao.getFromRedis(mobileNumber + CommonConstant.underline + validateCodeKey) + "\"}");
        logger.info("getValidateCode end");
    }

    @RequestMapping(value = "/delValidateCode", method = RequestMethod.GET)
    public void delValidateCode(HttpServletResponse response, String mobileNumber, String validateCodeKey){
        logger.info("delValidateCode start, parameter:" + mobileNumber + "," + validateCodeKey);

        dao.deleteFromRedis(mobileNumber + CommonConstant.underline + validateCodeKey);
        writer.writeObjectToJson(response, "{\"" + CommonConstant.result + "\":\"" + CommonConstant.success + "\"}");
        logger.info("delValidateCode end");
    }

    public String send(String phone, String content){
        return CommonConstant.success;
    }
}
