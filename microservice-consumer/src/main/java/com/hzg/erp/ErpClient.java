package com.hzg.erp;

import com.hzg.base.Client;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-erp", path = "/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @RequestMapping(value = "/getNo", method = RequestMethod.POST)
    String getNo(@RequestParam("prefix") String prefix);

    @Component
    class ErpClientFallback extends ClientFallback implements ErpClient {
        @Override
        public String getNo(String prefix) {
            log.info("getNo 异常发生，进入fallback方法，接收的参数：" + prefix);
            return "[]";
        }
    }
}
