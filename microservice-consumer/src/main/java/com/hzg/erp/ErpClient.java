package com.hzg.erp;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-erp", path = "/erp", fallback = ErpClient.ErpClientFallback.class)
public interface ErpClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ErpClient.class);

    @RequestMapping(value = "/getNo", method = RequestMethod.POST)
    String getNo(@RequestParam("prefix") String prefix);

    @RequestMapping(value = "/getSimpleNo", method = RequestMethod.POST)
    String getSimpleNo(@RequestParam("length") Integer length);

    @RequestMapping(value = "/entitiesSuggest", method = RequestMethod.POST)
    String entitiesSuggest(@RequestParam("targetEntities") String targetEntities, @RequestParam("entities") String entities,
                           @RequestBody String json);

    @RequestMapping(value = "/business", method = RequestMethod.POST)
    String business(@RequestParam("name") String name, @RequestBody String json);

    @RequestMapping(value = "/print", method = RequestMethod.POST)
    String print(@RequestParam("name") String name, @RequestBody String json);

    @RequestMapping(value = "/sfExpress/order/notify", method = RequestMethod.POST)
    String sfExpressOrderNotify(@RequestBody String json);

    @RequestMapping(value = "/privateQuery", method = RequestMethod.POST)
    String privateQuery(@RequestParam("entity") String entity, @RequestBody String json);

    @Component
    class ErpClientFallback extends ClientFallback implements ErpClient {
        @Override
        public String getNo(String prefix) {
            log.info("getNo 异常发生，进入fallback方法，接收的参数：" + prefix);
            return "[]";
        }

        @Override
        public String getSimpleNo(Integer length) {
            log.info("getSimpleNo 异常发生，进入fallback方法，接收的参数：" + length);
            return "[]";
        }

        @Override
        public String entitiesSuggest(String targetEntity, String entity, String json) {
            log.info("getNo 异常发生，进入fallback方法，接收的参数：" + targetEntity + "," + entity + "," + json);
            return "[]";
        }

        @Override
        public String business(String name, String json) {
            log.info("business 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行业务出错\"}";
        }

        @Override
        public String print(String name, String json) {
            log.info("print 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，打印出错\"}";
        }

        @Override
        public String sfExpressOrderNotify(String json) {
            logger.info("sfExpressOrderNotify 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行顺丰下单出错\"}";
        }

        @Override
        public String privateQuery(String entity, String json) {
            log.info("privateQuery 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{}";
        }
    }
}
