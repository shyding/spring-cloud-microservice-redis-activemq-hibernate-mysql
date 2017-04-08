package com.hzg.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用@FeignClient注解的fallback属性，指定fallback类
 * @author eacdy
 */
@FeignClient(name = "microservice-provider", fallback = UserClient.UserClientFallback.class)
public interface UserClient {
    @RequestMapping("/user/queryUsers")
    public List<User> queryUsers(User user);

    /**
     * 这边采取了和Spring Cloud官方文档相同的做法，将fallback类作为内部类放入Feign的接口中，当然也可以单独写一个fallback类。
     * @author eacdy
     */
    @Component
    static class UserClientFallback implements UserClient {
        private static final Logger LOGGER = LoggerFactory.getLogger(UserClientFallback.class);

        /**
         * hystrix fallback方法
         * @param user
         * @return 默认的用户
         */
        @Override
        public  List<User> queryUsers(User user) {
            UserClientFallback.LOGGER.info("异常发生，进入fallback方法，接收的参数：id = {}"+user);
            User nuser = new User();
            nuser.setId(-1);
            nuser.setUsername("");
            nuser.setAge(0);

            List users = new ArrayList();
            users.add(nuser);

            return users;
        }
    }
}
