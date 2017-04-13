package com.hzg.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 使用@FeignClient注解的fallback属性，指定fallback类
 */
@FeignClient(name = "microservice-provider-sys", fallback = SysClient.SysClientFallback.class)
public interface SysClient {
    @RequestMapping("/sys/query")
    String query(String entity, String json);

    @RequestMapping("/sys/update")
    String update(String entity, String json);

    @RequestMapping("/sys/save")
    String save(String entity, String json);

    /**
     * 这边采取了和Spring Cloud官方文档相同的做法，将fallback类作为内部类放入Feign的接口中，当然也可以单独写一个fallback类。
     */
    @Component
    class SysClientFallback implements SysClient {
        private static final Logger LOGGER = LoggerFactory.getLogger(SysClientFallback.class);

        /**
         * hystrix fallback方法
         * @param entity
         * @param json
         * @return 默认的用户
         */
        @Override
        public String query(String entity, String json) {
            SysClientFallback.LOGGER.info("query 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }

        @Override
        public String update(String entity, String json) {
            SysClientFallback.LOGGER.info("update 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }

        @Override
        public String save(String entity, String json) {
            SysClientFallback.LOGGER.info("save 异常发生，进入fallback方法，接收的参数：" + entity + ":" + json);
            return null;
        }
    }
}
