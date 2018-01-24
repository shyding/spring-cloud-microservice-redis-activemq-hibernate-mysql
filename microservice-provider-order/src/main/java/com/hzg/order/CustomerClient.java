package com.hzg.order;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-customer", path="/customer", fallback = CustomerClient.PayClientFallback.class)
public interface CustomerClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CustomerClient.class);

    @RequestMapping(value = "/unlimitedQuery", method = RequestMethod.POST)
    String unlimitedQuery(@RequestParam("entity") String entity, @RequestBody String json);

    @Component
    class PayClientFallback implements CustomerClient {
        @Override
        public String unlimitedQuery(String entity, String json) {
            logger.info("unlimitedQuery 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }
    }
}
