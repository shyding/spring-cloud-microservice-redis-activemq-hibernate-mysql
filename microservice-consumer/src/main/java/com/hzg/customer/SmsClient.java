package com.hzg.customer;

import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-sms", path="/sms", fallback = SmsClient.SmsClientFallback.class)
public interface SmsClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SmsClient.class);

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    String send(@RequestBody String json);

    @RequestMapping(value = "/generateValidateCode", method = RequestMethod.GET)
    String generateValidateCode(@RequestParam("length") Integer length, @RequestParam("mobileNumber") String mobileNumber);

    @RequestMapping(value = "/getValidateCode", method = RequestMethod.GET)
    String getValidateCode(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("validateCodeKey") String validateCodeKey);

    @RequestMapping(value = "/delValidateCode", method = RequestMethod.GET)
    String delValidateCode(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("validateCodeKey") String validateCodeKey);

    @Component
    class SmsClientFallback implements SmsClient {
        @Override
        public String send(@RequestBody String json) {
            logger.info("send 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，发送短信失败\"}";
        }

        @Override
        public String generateValidateCode(@RequestParam("length") Integer length, @RequestParam("mobileNumber") String mobileNumber) {
            logger.info("generateValidateCode 异常发生，进入fallback方法，接收的参数：" + length + "," + mobileNumber);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，生成短信验证码失败\"}";
        }

        @Override
        public String getValidateCode(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("validateCodeKey") String validateCodeKey) {
            logger.info("getValidateCode 异常发生，进入fallback方法，接收的参数：" + mobileNumber + "," + validateCodeKey);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，获取短信验证码失败\"}";
        }

        @Override
        public String delValidateCode(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("validateCodeKey") String validateCodeKey) {
            logger.info("delValidateCode 异常发生，进入fallback方法，接收的参数：" + mobileNumber + "," + validateCodeKey);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，删除短信验证码失败\"}";
        }
    }
}
