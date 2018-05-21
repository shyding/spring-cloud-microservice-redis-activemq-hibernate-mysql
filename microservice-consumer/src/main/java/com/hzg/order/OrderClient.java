package com.hzg.order;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-order", path="/order", fallback = OrderClient.OrderClientFallback.class)
public interface OrderClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OrderClient.class);

    @RequestMapping(value = "/querySaveResult", method = RequestMethod.GET)
    String querySaveResult(@RequestParam("orderSessionId") String orderSessionId);

    @RequestMapping(value = "/authorizeAmount", method = RequestMethod.POST)
    String authorizeAmount(String json);

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    String cancel(@RequestBody String json);

    @RequestMapping(value = "/paid", method = RequestMethod.POST)
    String paid(@RequestBody String json);

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    String audit(@RequestBody String json);

    @RequestMapping(value = "/business", method = RequestMethod.POST)
    String business(@RequestParam("name") String name, @RequestBody String json);

    @Component
    class OrderClientFallback extends ClientFallback implements OrderClient {
        @Override
        public String querySaveResult(String orderSessionId) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，查询订单保存结果出错\"}";
        }

        public String authorizeAmount(String json) {
            return "{\"" + CommonConstant.result + "\":\"系统异常，核定金额出错\"}";
        }

        public String cancel(String json) {
            log.info("cancel 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，取消订单出错\"}";
        }

        public String paid(String json) {
            log.info("paid 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，确认收款出错\"}";
        }

        public String audit(String json) {
            log.info("audit 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，审核通过自助单出错\"}";
        }

        @Override
        public String business(String name, String json) {
            log.info("business 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行业务出错\"}";
        }
    }
}
