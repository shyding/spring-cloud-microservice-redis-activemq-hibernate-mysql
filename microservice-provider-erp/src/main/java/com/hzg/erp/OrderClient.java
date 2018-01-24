package com.hzg.erp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-order", path="/order", fallback = OrderClient.OrderClientFallback.class)
public interface OrderClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OrderClient.class);

    @RequestMapping(value = "/getProductSoldQuantity", method = RequestMethod.POST)
    String getProductSoldQuantity(@RequestBody String json);

    @Component
    class OrderClientFallback implements OrderClient {
        @Override
        public String getProductSoldQuantity(String json) {
            logger.info("getProductSoldQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }
    }
}
