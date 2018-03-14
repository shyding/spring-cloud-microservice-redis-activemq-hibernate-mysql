package com.hzg.base;

import com.hzg.tools.CommonConstant;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface Client {
    org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Client.class);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    String update(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    String save(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/saveList", method = RequestMethod.POST)
    String saveList(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/suggest", method = RequestMethod.POST)
    String suggest(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/complexQuery", method = RequestMethod.POST)
    String complexQuery(@RequestParam("entity") String entity, @RequestBody String json, @RequestParam("position") int position, @RequestParam("rowNum") int rowNum);

    @RequestMapping(value = "/recordsSum", method = RequestMethod.POST)
    String recordsSum(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/isValueRepeat", method = RequestMethod.POST)
    String isValueRepeat(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    String delete(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/recover", method = RequestMethod.POST)
    String recover(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/unlimitedQuery", method = RequestMethod.POST)
    String unlimitedQuery(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/unlimitedSuggest", method = RequestMethod.POST)
    String unlimitedSuggest(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/unlimitedComplexQuery", method = RequestMethod.POST)
    String unlimitedComplexQuery(@RequestParam("entity") String entity, @RequestBody String json, @RequestParam("position") int position, @RequestParam("rowNum") int rowNum);

    @RequestMapping(value = "/unlimitedRecordsSum", method = RequestMethod.POST)
    String unlimitedRecordsSum(@RequestParam("entity") String entity, @RequestBody String json);

    class ClientFallback implements Client {
        @Override
        public String query(String entity, String json) {
            log.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[]";
        }

        @Override
        public String update(String entity, String json) {
            log.info("update 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，更新出错\"}";
        }

        @Override
        public String save(String entity, String json) {
            log.info("save 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，保存出错\"}";
        }

        @Override
        public String saveList(String entity, String json) {
            log.info("saveList 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，保存出错\"}";
        }

        @Override
        public String suggest(String entity, String json) {
            log.info("suggest 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，查询出错\"}";
        }

        @Override
        public String complexQuery(String entity, String json, int position, int rowNum) {
            log.info("complexQuery 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json + ", " + position + ", " + rowNum);
            return "[]";
        }

        @Override
        public String recordsSum(String entity, String json) {
            log.info("recordsSum 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json);
            return "{\"" + CommonConstant.recordsSum + "\": -1}";
        }

        @Override
        public String isValueRepeat(String entity, String json) {
            log.info("isValueRepeat 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json);
            return "{\"" + CommonConstant.result + "\": \"系统异常，查询出错\"}";
        }

        @Override
        public String delete(String entity, String json) {
            log.info("delete 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，作废出错\"}";
        }

        @Override
        public String recover(String entity, String json) {
            log.info("recover 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，恢复出错\"}";
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
