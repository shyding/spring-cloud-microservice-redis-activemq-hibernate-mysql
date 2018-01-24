package com.hzg.erp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-order", path="/afterSaleService", fallback = AfterSaleServiceClient.OrderClientFallback.class)
public interface AfterSaleServiceClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AfterSaleServiceClient.class);

    @RequestMapping(value = "/getProductOnReturnQuantity", method = RequestMethod.POST)
    String getProductOnReturnQuantity(@RequestBody String json);

    @RequestMapping(value = "/getProductReturnedQuantity", method = RequestMethod.POST)
    String getProductReturnedQuantity(@RequestBody String json);

    @Component
    class OrderClientFallback implements AfterSaleServiceClient {
        @Override
        public String getProductOnReturnQuantity(String json) {
            logger.info("getProductOnReturnQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getProductReturnedQuantity(String json) {
            logger.info("getProductReturnedQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }
    }
}
