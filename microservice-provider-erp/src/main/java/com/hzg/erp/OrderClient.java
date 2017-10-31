package com.hzg.erp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-Order", path="/order", fallback = OrderClient.PayClientFallback.class)
public interface OrderClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OrderClient.class);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);


    @Component
    class PayClientFallback implements OrderClient {
        @Override
        public String query(String entity, String json) {
            logger.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }
    }
}
