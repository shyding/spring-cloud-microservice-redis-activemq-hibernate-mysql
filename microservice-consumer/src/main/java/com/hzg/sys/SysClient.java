package com.hzg.sys;

import com.hzg.base.Client;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-sys", path="/sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/doSome", method = RequestMethod.POST)
    String doSome(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/queryPrivilege", method = RequestMethod.POST)
    String queryPrivilege(@RequestBody String json);

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    String signIn(@RequestBody String json);

    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    String signOut(@RequestBody String json);

    @RequestMapping(value = "/hasLoginDeal", method = RequestMethod.POST)
    String hasLoginDeal(@RequestBody String json);

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    String audit(@RequestBody String json);

    @Component
    class SysClientFallback extends ClientFallback implements SysClient {
        @Override
        public String doSome(String entity, String json) {
            logger.info("doSome 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }

        @Override
        public String queryPrivilege(@RequestBody String json) {
            logger.info("queryPrivilege 异常发生，进入fallback方法，接收的参数："  + ":" + json);
            return "{\"result\":\"系统异常，查询出错\"}";
        }

        @Override
        public String signIn(@RequestBody String json) {
            return "{\"result\":\"系统异常，登录出错\"}";
        }

        @Override
        public String signOut(@RequestParam("sessionId") String sessionId) {
            return "{\"result\":\"系统异常，注销出错\"}";
        }

        @Override
        public String hasLoginDeal(@RequestParam("sessionId") String sessionId) {
            return "{\"result\":\"系统异常，处理重复登录出错\"}";
        }

        @Override
        public String audit(@RequestParam("sessionId") String sessionId) {
            return "{\"result\":\"系统异常，办理事宜出错\"}";
        }
    }
}
