package com.hzg.erp;

import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-sys", path="/sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SysClient.class);

    @RequestMapping(value = "/launchAuditFlow", method = RequestMethod.POST)
    String launchAuditFlow(@RequestBody String json);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    String update(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    String delete(@RequestParam("entity") String entity, @RequestBody String json);

    @Component
    class SysClientFallback implements SysClient {
        @Override
        public String launchAuditFlow(String json) {
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，发起事宜出错\"}";
        }

        @Override
        public String query(String entity, String json) {
            logger.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }

        @Override
        public String update(String entity, String json) {
            logger.info("update 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，更新出错\"}";
        }

        @Override
        public String delete(String entity, String json) {
            logger.info("delete 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail + ",系统异常，更新出错\"}";
        }
    }
}
