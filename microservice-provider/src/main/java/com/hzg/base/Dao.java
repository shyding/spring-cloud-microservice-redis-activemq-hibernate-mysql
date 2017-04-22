package com.hzg.base;

/**
 * Created by Administrator on 2017/4/20.
 */

import com.hzg.tools.ObjectToSql;
import org.hibernate.criterion.Example;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author smjie
 * @version 1.00
 * @Date 2017/4/20
 */
@Repository
public class Dao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectToSql objectToSql;


    public String save(Object object, Class clazz, String redisKeyPerfix){
        sessionFactory.getCurrentSession().save(object);

        Object dbObject = sessionFactory.getCurrentSession().createCriteria(clazz).
                add(Example.create(object).excludeZeroes()).uniqueResult();
        if (dbObject != null) {
            try {
                redisTemplate.opsForValue().set(redisKeyPerfix + "_" + String.valueOf(clazz.getMethod("getId").invoke(object)), dbObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "success";
    }

    public String updateById(Integer id, Object object, Class clazz, String redisKeyPerfix){
        //更新数据库记录后，同时重新设置redis缓存
        int result = sessionFactory.getCurrentSession().createSQLQuery(
                objectToSql.generateUpdateSqlByAnnotation(object, "id=" + id)).executeUpdate();

        Object dbObject = sessionFactory.getCurrentSession().createCriteria(clazz).
                add(Example.create(object).excludeZeroes()).uniqueResult();
        if (dbObject != null) {
            redisTemplate.opsForValue().set(redisKeyPerfix + "_" + id, dbObject);
        }

        return (result > 0 ? "success" : "fail") + "," + result + " item updated";
    }

    public List query(Integer id, Object object, Class clazz, String redisKeyPerfix){
        List objects = new ArrayList<>();

        if (id != null) {
            Object dbObject = queryById(id, object, clazz, redisKeyPerfix);
            if (dbObject != null) {
                objects.add(dbObject);
            }

            return objects;
        }

        return sessionFactory.getCurrentSession().createCriteria(clazz).add(Example.create(object).excludeZeroes()).list();
    }

    public Object queryById(Integer id, Object object, Class clazz, String redisKeyPerfix){
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();

        // redis 里没有缓存 object，则从数据库里查询 object，同时设置查询到的 object 到 redis
        Object dbObject = valueOperation.get(redisKeyPerfix + id);
        if (dbObject == null) {
            dbObject =  sessionFactory.getCurrentSession().get(clazz, id);
            if (dbObject != null) {
                valueOperation.set(redisKeyPerfix + "_" + id, dbObject);
            }
        }

        return dbObject;
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
