package com.hzg.user;

import com.hzg.tools.ObjectToSql;
import org.hibernate.criterion.Example;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/3/16.
 */
@Repository
public class UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectToSql objectToSql;

    public String redisKeyPerfix = "user_";

    public User queryUser(User user){
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();

        // redis 里没有缓存 user，则从数据库里查询 user，同时设置查询到的 user 到 redis
        User dbUser = (User) valueOperation.get(redisKeyPerfix + user.getId());
        if (dbUser == null) {
            dbUser =  (User)sessionFactory.getCurrentSession().createCriteria(User.class).
                    add(Example.create(user).excludeZeroes()).uniqueResult();

            if (dbUser != null) {
                valueOperation.set(redisKeyPerfix + user.getId(), dbUser);
            }
        }

        return dbUser;
    }

    public int updateUser(User user){
        //更新数据库记录后，同时重新设置redis缓存
        int result = sessionFactory.getCurrentSession().createSQLQuery(
                objectToSql.generateUpdateSqlByAnnotation(user, "id=" + user.getId())).executeUpdate();

        User dbUser = (User)sessionFactory.getCurrentSession().createCriteria(User.class).
                      add(Example.create(user).excludeZeroes()).uniqueResult();
        if (dbUser != null) {
            redisTemplate.opsForValue().set(redisKeyPerfix + user.getId(), dbUser);
        }

        return result;
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
