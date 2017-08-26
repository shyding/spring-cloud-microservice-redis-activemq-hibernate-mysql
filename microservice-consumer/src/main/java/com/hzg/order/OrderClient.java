package com.hzg.order;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-provider-order", path="/order", fallback = OrderClient.OrderClientFallback.class)
public interface OrderClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OrderClient.class);

    @RequestMapping(value = "/saveResult", method = RequestMethod.GET)
    String saveResult(String orderSessionId);

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    String cancel(String json);


    @Component
    class OrderClientFallback extends ClientFallback implements OrderClient {
        @Override
        public String saveResult(String orderSessionId) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，查询订单保存结果出错\"}";
        }

        public String cancel(String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，取消订单出错\"}";
        }
    }
}
