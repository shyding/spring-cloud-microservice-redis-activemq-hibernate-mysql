package com.hzg.sys;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-erp", path="/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @RequestMapping(value = "/auditAction", method = RequestMethod.POST)
    String auditAction(@RequestBody String json);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);

    @Component
    class ErpClientFallback implements ErpClient {
        @Override
        public String auditAction(String json) {
            logger.info("auditAction 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"result\":\"系统异常，更新出错\"}";
        }

        @Override
        public String query(String entity, String json) {
            logger.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }
    }
}
