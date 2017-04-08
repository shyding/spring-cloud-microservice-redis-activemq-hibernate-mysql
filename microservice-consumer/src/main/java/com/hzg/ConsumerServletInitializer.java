package com.hzg;

/**
 * Created by Administrator on 2017/4/8.
 * SpringBootServletInitializer 是一个支持
 * Spring Boot的Spring  WebApplicationInitializer 实现。除了配置Spring的 Dispatcher-
 * Servlet ， SpringBootServletInitializer 还会在Spring应用程序上下文里查找 Filter 、
 * Servlet 或 ServletContextInitializer 类型的Bean，把它们绑定到Servlet容器里
 * 有这个类以后，Spring Boot 应用程序才可以部署到 tomcat 等web容器里
 */
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
public class ConsumerServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ConsumerApplication.class);
    }
}