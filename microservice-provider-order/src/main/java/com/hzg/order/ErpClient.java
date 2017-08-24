package com.hzg.order;

import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-erp", path="/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @RequestMapping(value = "/getStockQuantity", method = RequestMethod.POST)
    String getStockQuantity(@RequestBody String json);

    @Component
    class ErpClientFallback implements ErpClient {
        @Override
        public String getStockQuantity(String json) {
            logger.info("getStockQuantity 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，更新出错\"}";
        }
    }
}
