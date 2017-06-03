package com.hzg.base;

/**
 * Created by Administrator on 2017/4/20.
 */
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class Dao {
    private Logger logger = Logger.getLogger(Dao.class);

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    /**
     * 把 对象 存储到 redis
     * @param key
     * @param object
     */
    public void storeToRedis(String key, Object object) {
        if (object != null) {
            redisTemplate.opsForValue().set(key, object);
        }
    }

    /**
     * 从 redis 得到对象
     * @param key
     * @return
     */
    public Object getFromRedis(String key) {
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
        return  valueOperation.get(key);
    }


    /**
     * 把 对象 存储到 redis 里 seconds 秒
     * @param key
     * @param object
     * @param seconds
     */
    public void storeToRedis(String key, Object object, int seconds){
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        //设置值
        boundValueOperations.set(object);
        //设置过期时间
        boundValueOperations.expire(seconds, TimeUnit.SECONDS);
    }

    /**
     * 删除 key 对应的对象
     * @param key
     */
    public void deleteFromRedis(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }
}
