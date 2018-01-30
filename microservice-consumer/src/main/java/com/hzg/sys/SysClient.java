package com.hzg.sys;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
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

    @RequestMapping(value = "/user/signIn", method = RequestMethod.POST)
    String signIn(@RequestBody String json);

    @RequestMapping(value = "/user/signOut", method = RequestMethod.POST)
    String signOut(@RequestBody String json);

    @RequestMapping(value = "/user/hasLoginDeal", method = RequestMethod.POST)
    String hasLoginDeal(@RequestBody String json);

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    String audit(@RequestBody String json);

    @RequestMapping(value = "/getPostByUri", method = RequestMethod.POST)
    String getPostByUri(@RequestBody String json);

    @RequestMapping(value = "/privateQuery", method = RequestMethod.POST)
    String privateQuery(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/computeSysCurrentTimeMillis", method = RequestMethod.GET)
    long computeSysCurrentTimeMillis();

    @Component
    class SysClientFallback extends ClientFallback implements SysClient {
        @Override
        public String privateQuery(String entity, String json) {
            log.info("privateQuery 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }

        @Override
        public String doSome(String entity, String json) {
            logger.info("doSome 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }

        @Override
        public String queryPrivilege(@RequestBody String json) {
            logger.info("queryPrivilege 异常发生，进入fallback方法，接收的参数："  + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，查询出错\"}";
        }

        @Override
        public String signIn(@RequestBody String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，登录出错\"}";
        }

        @Override
        public String signOut(@RequestBody String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，注销出错\"}";
        }

        @Override
        public String hasLoginDeal(@RequestBody String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，处理重复登录出错\"}";
        }

        @Override
        public String audit(@RequestBody String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，办理事宜出错\"}";
        }

        @Override
        public String getPostByUri(@RequestBody String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，查询岗位出错\"}";
        }

        @Override
        public long computeSysCurrentTimeMillis() {
            logger.info("computeSysCurrentTimeMillis 异常发生，进入fallback方法");
            return -1L;
        }
    }
}
