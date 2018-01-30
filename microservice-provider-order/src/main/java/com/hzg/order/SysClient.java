package com.hzg.order;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-sys", path="/sys", fallback = SysClient.ErpClientFallback.class)
public interface SysClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/getCompanyByUser", method = RequestMethod.POST)
    String getCompanyByUser(@RequestBody String json);

    @RequestMapping(value = "/computeSysCurrentTimeMillis", method = RequestMethod.GET)
    long computeSysCurrentTimeMillis();

    @Component
    class ErpClientFallback implements SysClient {
        @Override
        public String getCompanyByUser(String json) {
            logger.info("getCompanyByUser 异常发生，进入fallback方法，接收的参数：" + json);
            return "{}";
        }

        @Override
        public long computeSysCurrentTimeMillis() {
            logger.info("computeSysCurrentTimeMillis 异常发生，进入fallback方法");
            return -1L;
        }
    }
}
