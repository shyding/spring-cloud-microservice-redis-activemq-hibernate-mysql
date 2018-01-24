package com.hzg.afterSaleService;

import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-provider-order", path="/afterSaleService", fallback = AfterSaleServiceClient.AfterSaleServiceClientFallback.class)
public interface AfterSaleServiceClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AfterSaleServiceClient.class);

    @RequestMapping(value = "/business", method = RequestMethod.POST)
    String business(@RequestParam("name") String name, @RequestBody String json);

    @RequestMapping(value = "/unlimitedQuery", method = RequestMethod.POST)
    String unlimitedQuery(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/unlimitedSuggest", method = RequestMethod.POST)
    String unlimitedSuggest(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/unlimitedComplexQuery", method = RequestMethod.POST)
    String unlimitedComplexQuery(@RequestParam("entity") String entity, @RequestBody String json, @RequestParam("position") int position, @RequestParam("rowNum") int rowNum);

    @RequestMapping(value = "/unlimitedRecordsSum", method = RequestMethod.POST)
    String unlimitedRecordsSum(@RequestParam("entity") String entity, @RequestBody String json);


    @Component
    class AfterSaleServiceClientFallback extends ClientFallback implements AfterSaleServiceClient {
        @Override
        public String business(String name, String json) {
            log.info("business 异常发生，进入fallback方法，接收的参数：" + name + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，执行业务出错\"}";
        }

        @Override
        public String unlimitedQuery(String entity, String json) {
            log.info("unlimitedQuery 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }

        @Override
        public String unlimitedSuggest(String entity, String json) {
            log.info("unlimitedSuggest 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，保存出错\"}";
        }

        @Override
        public String unlimitedComplexQuery(String entity, String json, int position, int rowNum) {
            log.info("unlimitedComplexQuery 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json + ", " + position + ", " + rowNum);
            return "[]";
        }

        @Override
        public String unlimitedRecordsSum(String entity, String json) {
            log.info("unlimitedRecordsSum 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json);
            return "{\"" + CommonConstant.recordsSum + "\": -1}";
        }
    }
}
