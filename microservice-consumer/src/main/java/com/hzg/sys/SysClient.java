package com.hzg.sys;

import com.hzg.base.Client;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/sys/doSome", method = RequestMethod.POST)
    String doSome(@RequestParam("entity") String entity, @RequestBody String json);

    @Component
    class SysClientFallback extends ClientFallback implements SysClient  {
        @Override
        public String doSome(String entity, String json) {
            logger.info("doSome 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }
    }
}
