package com.hzg.erp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-sys", path="/sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    String audit(@RequestBody String json);

    @Component
    class SysClientFallback implements SysClient {
        @Override
        public String audit(String json) {
            return "{\"result\":\"系统异常，保存事宜出错\"}";
        }
    }
}
