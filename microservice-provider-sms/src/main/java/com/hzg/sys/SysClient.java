package com.hzg.sys;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-sys", path="/sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/computeSysCurrentTimeMillis", method = RequestMethod.GET)
    long computeSysCurrentTimeMillis();

    @Component
    class SysClientFallback implements SysClient {

        @Override
        public long computeSysCurrentTimeMillis() {
            logger.info("computeSysCurrentTimeMillis 异常发生，进入fallback方法");
            return -1L;
        }
    }
}
