package com.hzg.erp;

import com.hzg.base.Client;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name = "microservice-provider-erp", path = "/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @Component
    class ErpClientFallback extends ClientFallback implements ErpClient {
    }
}
