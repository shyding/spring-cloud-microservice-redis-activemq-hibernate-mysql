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

    @RequestMapping(value = "/getPurchaseProductOnReturnQuantity", method = RequestMethod.POST)
    String getPurchaseProductOnReturnQuantity(@RequestBody String json);

    @RequestMapping(value = "/getPurchaseProductReturnedQuantity", method = RequestMethod.POST)
    String getPurchaseProductReturnedQuantity(@RequestBody String json);

    @RequestMapping(value = "/getProductOnChangeQuantity", method = RequestMethod.POST)
    String getProductOnChangeQuantity(@RequestBody String json);

    @RequestMapping(value = "/getProductOnChangeOnReturnQuantity", method = RequestMethod.POST)
    String getProductOnChangeOnReturnQuantity(@RequestBody String json);

    @RequestMapping(value = "/getProductChangedQuantity", method = RequestMethod.POST)
    String getProductChangedQuantity(@RequestBody String json);

    @RequestMapping(value = "/getLastValidReturnProductByProduct", method = RequestMethod.POST)
    String getLastValidReturnProductByProduct(@RequestBody String json);

    @RequestMapping(value = "/getLastValidChangeProductByProduct", method = RequestMethod.POST)
    String getLastValidChangeProductByProduct(@RequestBody String json);

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


        @Override
        public String getPurchaseProductOnReturnQuantity(String json) {
            logger.info("getPurchaseProductOnReturnQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getPurchaseProductReturnedQuantity(String json) {
            logger.info("getPurchaseProductReturnedQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getProductOnChangeQuantity(String json) {
            logger.info("getProductOnChangeQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getProductOnChangeOnReturnQuantity(String json) {
            logger.info("getProductOnChangeOnReturnQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getProductChangedQuantity(String json) {
            logger.info("getProductChangedQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getLastValidReturnProductByProduct(String json) {
            logger.info("getLastValidReturnProductByProduct 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public String getLastValidChangeProductByProduct(String json) {
            logger.info("getLastValidChangeProductByProduct 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }
    }
}
