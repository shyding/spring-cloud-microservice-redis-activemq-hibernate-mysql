package com.hzg.base;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface Client {
    org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Client.class);

    @RequestMapping(value = "/sys/query", method = RequestMethod.POST)
    String query(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/sys/update", method = RequestMethod.POST)
    String update(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/sys/save", method = RequestMethod.POST)
    String save(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/sys/suggest", method = RequestMethod.POST)
    String suggest(@RequestParam("entity") String entity, @RequestBody String json);

    @RequestMapping(value = "/sys/complexQuery", method = RequestMethod.POST)
    String complexQuery(@RequestParam("entity") String entity, @RequestBody String json, @RequestParam("position") int position, @RequestParam("rowNum") int rowNum);

    @RequestMapping(value = "/sys/recordsSum", method = RequestMethod.POST)
    String recordsSum(@RequestParam("entity") String entity, @RequestBody String json);

    class ClientFallback implements Client {
        @Override
        public String query(String entity, String json) {
            log.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "[{}]";
        }

        @Override
        public String update(String entity, String json) {
            log.info("update 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"result\":\"系统异常，更新出错\"}";
        }

        @Override
        public String save(String entity, String json) {
            log.info("save 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"result\":\"系统异常，保存出错\"}";
        }

        @Override
        public String suggest(String entity, String json) {
            log.info("suggest 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return "{\"result\":\"系统异常，保存出错\"}";
        }

        @Override
        public String complexQuery(String entity, String json, int position, int rowNum) {
            log.info("complexQuery 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json + ", " + position + ", " + rowNum);
            return "[{}]";
        }

        @Override
        public String recordsSum(String entity, String json) {
            log.info("complexQuery 异常发生，进入fallback方法，接收的参数：" + entity + ", " + json);
            return "{\"recordsSum\": -1}";
        }
    }
}
