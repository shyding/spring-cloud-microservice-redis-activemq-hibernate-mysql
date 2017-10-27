package com.hzg.order;

import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-erp", path="/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @RequestMapping(value = "/querySalePrice", method = RequestMethod.GET)
    String querySalePrice(@RequestBody String json);

    @RequestMapping(value = "/getStockQuantity", method = RequestMethod.POST)
    String getStockQuantity(@RequestBody String json);

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    String save(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/business", method = RequestMethod.POST)
    String business(@RequestParam("name") String name, @RequestBody String json);

    @RequestMapping(value = "/getNo", method = RequestMethod.POST)
    String getNo(@RequestParam("prefix") String prefix);

    @RequestMapping(value = "/getWarehouseByCompany", method = RequestMethod.GET)
    String getWarehouseByCompany(@RequestBody String json);


    @Component
    class ErpClientFallback implements ErpClient {
        @Override
        public String querySalePrice(String json) {
            logger.info("querySalePrice 异常发生，进入fallback方法，接收的参数：" + json);
            return "[]";
        }

        @Override
        public String getStockQuantity(String json) {
            logger.info("getStockQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，更新出错\"}";
        }

        @Override
        public String save(String entity, String json) {
            logger.info("save 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，保存出错\"}";
        }

        @Override
        public String business(String name, String json) {
            logger.info("business 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行业务出错\"}";
        }

        @Override
        public String getNo(String prefix) {
            logger.info("getNo 异常发生，进入fallback方法，接收的参数：" + prefix);
            return "[]";
        }

        @Override
        public String getWarehouseByCompany(String json) {
            logger.info("getWarehouseByCompany 异常发生，进入fallback方法，接收的参数：" + json);
            return "[]";
        }
    }
}
