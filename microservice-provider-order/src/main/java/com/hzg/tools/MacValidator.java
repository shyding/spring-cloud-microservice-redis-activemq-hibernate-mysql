package com.hzg.tools;

import com.hzg.sys.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MacValidator {
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    public boolean md5Validate(String mac, String sessionId, String validateStr) {
        boolean pass = false;

        String salt = (String)redisTemplate.opsForValue().get(CommonConstant.salt + CommonConstant.underline + sessionId);
        com.hzg.customer.User user = (com.hzg.customer.User) redisTemplate.opsForValue().get((String)redisTemplate.opsForValue().get(CommonConstant.sessionId + CommonConstant.underline + sessionId));
        String pin = DigestUtils.md5Hex(salt + user.getPassword()).toUpperCase();

        if (mac.equals(DigestUtils.md5Hex(validateStr + pin).toUpperCase())) {
            pass = true;
        }

        return pass;
    }
}
