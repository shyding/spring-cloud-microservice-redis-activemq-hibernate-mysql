package com.hzg;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPoolConfig;

import javax.jms.Queue;
import javax.sql.DataSource;
import java.util.*;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJms
@RestController
@EnableTransactionManagement
@EnableFeignClients
@EnableCircuitBreaker
@ComponentScan({"com.hzg", "com.boyuanitsm.pay", "net.coupirum.io.httprequest"})
public class PayApplication {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String show_sql;

    @Value("${hibernate.current_session_context_class}")
    private String current_session_context_class;

    @Value("${redis.cluster.nodes}")
    private String nodes;
    @Value("${redis.cluster.max-redirects}")
    private Integer maxRedirects;

    @Value("${redis.pool.max-total}")
    private Integer maxTotal;
    @Value("${redis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${redis.pool.max-wait}")
    private Integer maxWait;
    @Value("${redis.pool.test-on-borrow}")
    private boolean testOnBorrow;

    // 设置 mq 队列
    @Bean
    public Queue queue() {
        return new ActiveMQQueue("userQueue");
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory (){
        Set<RedisNode> redisNodes = new HashSet<>();
        String[] nodesArr = nodes.split(";");
        for (String node:nodesArr){
            String[] parts= StringUtils.split(node,":");
            redisNodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
        }
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        clusterConfig.setMaxRedirects(maxRedirects);
        clusterConfig.setClusterNodes(redisNodes);


        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTestOnBorrow(testOnBorrow);

        return new JedisConnectionFactory(clusterConfig, poolConfig);
    }

    @Autowired
    private JedisConnectionFactory connectionFactory;

    // 设置 redisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);

        return redisTemplate;
    }

    // 设置 redisTemplate
    @Bean
    public RedisTemplate<String, Long> redisTemplateLong() {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));

        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);

        return stringRedisTemplate;
    }

    // 设置 hibernate sessionFactory
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();

        localSessionFactoryBean.setDataSource(dataSource);
        localSessionFactoryBean.setPackagesToScan("com.hzg.*");
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", dialect);
        hibernateProperties.setProperty("hibernate.show_sql", show_sql);
        hibernateProperties.setProperty("current_session_context_class", current_session_context_class);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties);

        return localSessionFactoryBean;
    }

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    // 设置事物
    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory.getObject());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation()
    {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    // 注册过滤器
    @Bean
    public FilterRegistrationBean indexFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new OpenSessionInViewFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * 本地服务实例的信息
     * @return
     */
    @GetMapping("/instance-info")
    public ServiceInstance showInfo() {
        ServiceInstance localServiceInstance = this.discoveryClient.
                getLocalServiceInstance();
        return localServiceInstance;
    }

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
