package com.hzg.erp;

import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-pay", path="/pay", fallback = PayClient.PayClientFallback.class)
public interface PayClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PayClient.class);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    String update(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    String save(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/saveSplitAmountPays", method = RequestMethod.POST)
    String saveSplitAmountPays(@RequestParam("amount") Float amount, @RequestBody String json);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    String delete(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/offlinePaid", method = RequestMethod.POST)
    String offlinePaid(@RequestBody String json);

    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    String refund(@RequestParam("entity") String entity, @RequestParam("entityId") Integer entityId,
                  @RequestParam("amount") Float amount, @RequestBody String json);


    @Component
    class PayClientFallback implements PayClient {
        @Override
        public String query(String entity, String json) {
            logger.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }

        @Override
        public String update(String entity, String json) {
            logger.info("update 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，更新出错\"}";
        }

        @Override
        public String save(String entity, String json) {
            logger.info("save 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，保存出错\"}";
        }

        @Override
        public String saveSplitAmountPays(Float amount, String json) {
            logger.info("saveSplitAmountPays 异常发生，进入fallback方法，接收的参数：" + amount + "," + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，保存出错\"}";
        }

        @Override
        public String delete(String entity, String json) {
            logger.info("delete 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，保存出错\"}";
        }

        @Override
        public String offlinePaid(String json) {
            logger.info("offlinePaid 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，保存出错\"}";
        }

        @Override
        public String refund(String entity, Integer entityId, Float amount, String json) {
            logger.info("refund 异常发生，进入fallback方法，接收的参数：" + entity + "," + entityId + "," + amount);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，保存出错\"}";
        }
    }
}
