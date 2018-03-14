package com.hzg.customer;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-customer", path="/customer", fallback = CustomerClient.CustomerClientFallback.class)
public interface CustomerClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CustomerClient.class);

    @RequestMapping(value = "/user/signIn", method = RequestMethod.POST)
    String signIn(@RequestBody String json);

    @RequestMapping(value = "/user/signOut", method = RequestMethod.POST)
    String signOut(@RequestBody String json);

    @RequestMapping(value = "/user/hasLoginDeal", method = RequestMethod.POST)
    String hasLoginDeal(@RequestBody String json);

    @RequestMapping(value = "/business", method = RequestMethod.POST)
    String business(@RequestParam("name") String name, @RequestBody String json);

    @Component
    class CustomerClientFallback extends ClientFallback implements CustomerClient {
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
        public String business(String name, String json) {
            log.info("business 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行业务出错\"}";
        }
    }
}
