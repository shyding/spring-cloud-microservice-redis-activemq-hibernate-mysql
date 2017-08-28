package com.hzg.order;

import com.hzg.base.Dao;
import com.hzg.tools.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class OrderDao extends Dao {

    Logger logger = Logger.getLogger(OrderDao.class);

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    public RedisTemplate<String, Long> redisTemplateLong;

    private String currentDay = "";
    private int countLength = 3;

    private RedisAtomicLong counter;

    /**
     * 产生一个编号
     * @param prefix 编号前缀，如采购单的编号前缀：CG
     * @return
     */
    public String getNo(String prefix) {
        String key = "counter_" + prefix;

        Long count = 1L;
        Long value = redisTemplateLong.opsForValue().get(key);

        if (value == null) {
            counter = new RedisAtomicLong(key, redisTemplateLong.getConnectionFactory(), count);
            counter.expireAt(dateUtil.getDay(1));
            currentDay = dateUtil.getCurrentDayStr("yyMMdd");

        } else {
            if (counter == null) {
                counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
                counter.set(value);
                counter.expireAt(dateUtil.getDay(1));
                currentDay = dateUtil.getCurrentDayStr("yyMMdd");
            }

            count = counter.incrementAndGet();
        }


        String countStr = String.valueOf(count);

        int minusLength = countLength - countStr.length();
        while (minusLength > 0) {
            countStr = "0" + countStr;
            --minusLength;
        }

        String no = prefix + currentDay + countStr;

        logger.info("generate no:" + no);

        return no;
    }

    /**
     * 获取多个值
     * @param key
     * @return
     */
    public List<Object> getValuesFromList(String key) {
        List<Object> values = new ArrayList<>();
        List<Object> nullValueKeys = new ArrayList<>();

        BoundListOperations<String, Object> listOps = redisTemplate.boundListOps(key);

        List keys = listOps.range(0, -1); // -1表示获取所有元素
        for (int i = 0; i < keys.size(); i++) {
            Object value = getFromRedis((String)keys.get(i));

            if (value != null) {
                values.add(value);

            } else {
               nullValueKeys.add(keys.get(i));
            }
        }

        if (nullValueKeys.size() == keys.size()) {
            deleteFromRedis(key);

        } else {
            for (Object nullValueKey : nullValueKeys) {
                listOps.remove(0, nullValueKey);  // 0表示去除所有
            }
        }

        return values;
    }

    /**
     * 获取多个值
     * @param key
     * @return
     */
    public void putKeyToList(String listKey, Object key) {
        redisTemplate.boundListOps(listKey).leftPush(key);
    }
}
