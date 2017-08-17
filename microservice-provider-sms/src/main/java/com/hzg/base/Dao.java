package com.hzg.base;

/**
 * Created by Administrator on 2017/4/20.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class Dao {
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    /**
     * 从 redis 得到对象
     * @param key
     * @return
     */
    public Object getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 从 redis 删除对象
     * @param key
     * @return
     */
    public void deleteFromRedis(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 把 对象 存储到 redis 里 seconds 秒
     * @param key
     * @param object
     */
    public void storeToRedisAtDate(String key, Object object, Date date){
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        //设置值
        boundValueOperations.set(object);
        //设置过期时间
        boundValueOperations.expireAt(date);
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
     * 获取 key 的过期时间
     * @param key
     */
    public long getExpire(String key){
        long expire = 0;

        try {
            expire = redisTemplate.boundValueOps(key).getExpire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return expire;
    }

    /**
     * 获取发送次数
     * @param key
     * @return
     */
    public int getSendCount(String key) {
        Object countObj = redisTemplate.opsForValue().get(key);

        if (countObj != null) {
            return (Integer)countObj;
        } else {
            return 0;
        }
    }


    /**
     * BoundKeyOperations、BoundValueOperations、BoundSetOperations
     * BoundListOperations、BoundSetOperations、BoundHashOperations
     */
    public void testBoundOperations(){
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps("BoundTest");
        //设置值
        boundValueOperations.set("test12345");
        //设置过期时间
        boundValueOperations.expire(100, TimeUnit.SECONDS);
        //重命名Key
//        boundValueOperations.rename("BoundTest123");

        System.out.println("key: " + boundValueOperations.getKey());
        System.out.println(boundValueOperations.get());
        System.out.println("expire: " + boundValueOperations.getExpire());
    }
}
