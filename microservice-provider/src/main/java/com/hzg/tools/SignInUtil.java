package com.hzg.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class SignInUtil {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    //如果用户失败登录次数 >= waitToSignInCount，则需要等待 2^signInFailCount * waitTimeUnit 毫秒，才可以再次登录
    public  int waitToSignInCount = 3;
    public  int waitTimeUnit = 10 * 1000;

    /**
     * 取到用户等待一段时间
     *
     * @param username
     */
    public long userWait(String username) {
        Long signInFailTime = (Long)redisTemplate.opsForValue().get("signInFailTime_" + username);
        if (signInFailTime != null) {
            long timeDiff = System.currentTimeMillis() - signInFailTime;
            long waitTime = getWaitTime(username);

            if (timeDiff < waitTime) {
                return (waitTime - timeDiff);
            }
        }

        return 0;
    }

    /**
     * 设置登录失败次数。及失败登录次数 >= waitToSignInCount时，该次登录的登录时间
     *
     * @param username
     */
    public void setSignInFailInfo(String username) {
        Integer signInFailCount = (Integer) redisTemplate.opsForValue().get("signInFailCount_" + username);
        if (signInFailCount == null) {
            redisTemplate.opsForValue().set("signInFailCount_" + username, 1);

        } else {
            signInFailCount = signInFailCount + 1;
            redisTemplate.opsForValue().set("signInFailCount_" + username, signInFailCount);

            if (signInFailCount >= waitToSignInCount) {
                redisTemplate.opsForValue().set("signInFailTime_" + username, System.currentTimeMillis());
            }
        }
    }

    public void removeSignInFailInfo(String username) {
        if (redisTemplate.opsForValue().get("signInFailCount_" + username) != null) {
            redisTemplate.opsForValue().getOperations().delete("signInFailCount_" + username);
        }
        if (redisTemplate.opsForValue().get("signInFailTime_" + username) != null) {
            redisTemplate.opsForValue().getOperations().delete("signInFailTime_" + username);
        }
    }

    public  int getSignInFailCount(String username) {
        Integer signInFailCount = (Integer) redisTemplate.opsForValue().get("signInFailCount_" + username);
        return signInFailCount == null ? 0 : signInFailCount;
    }

    /**
     * 获取等待时间(毫秒)
     * @param username
     * @return
     */
    public  long getWaitTime(String username) {
        long waitTime = 0;

        int signInFailCount = getSignInFailCount(username);
        if (signInFailCount >= waitToSignInCount) {
            waitTime = (int)Math.pow(2, signInFailCount) * waitTimeUnit;
        }

        return waitTime;
    }

    /**
     * 获取等待秒数
     * @param username
     * @return
     */
    public long getWaitSeconds(String username) {
        return getWaitTime(username)/1000;
    }

    public  int removedSignInFailCount = 10;

    /**
     * 每隔1个小时，移除登录失败次数 >= 10（等待2^10 * waitTimeUnit毫秒）的用户对应的 map 元素
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void clearMap(){
        Set<String> keys =  redisTemplate.keys("signInFailCount_*");
        for (String key : keys) {
            if ((Integer)redisTemplate.opsForValue().get(key) > removedSignInFailCount) {
                removeSignInFailInfo(key.substring(key.indexOf("_")+1));
            }
        }
    }


    /**
     * 用户会话设置
     */

    /**
     * 判断用户是否存在
     * @param username
     */
    public  boolean isUserExist(String username) {
        return redisTemplate.opsForValue().get("user_" + username) != null;
    }

    /**
     * 设置用户
     * @param sessionId
     * @param username
     */
    public  void setUser(String sessionId, String username) {
        if (!isUserExist(username)) {
            redisTemplate.boundValueOps("sessionId_" + sessionId).set(username, 1800, TimeUnit.SECONDS);
            redisTemplate.boundValueOps("user_" + username).set(sessionId, 1800, TimeUnit.SECONDS);
        }
    }

    /**
     * 移除用户
     * @param username
     */
    public void removeUser(String username) {
        if (isUserExist(username)) {
            redisTemplate.opsForValue().getOperations().delete("sessionId_" + (String)redisTemplate.opsForValue().get("user_" + username));
            redisTemplate.opsForValue().getOperations().delete("user_" + username);
        }
    }

    /**
     * 根据用户名取得 sessionId
     * @param username
     * @return
     */
    public  String getSessionIdByUser(String username) {
        return (String)redisTemplate.opsForValue().get("user_" + username);
    }

}