package com.hzg.erp;

import com.hzg.base.Dao;
import com.hzg.tools.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.util.*;

@Repository
public class ErpDao extends Dao {

    Logger logger = Logger.getLogger(ErpDao.class);

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    public RedisTemplate<String, Long> redisTemplateLong;

    private String currentDay = "";
    private String sfTransMessage_currentDay = "";
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
     * 顺丰交易流水号格式如：YYYYMMDDHHmmss+流水号{4},例如:201811121633230001,交易流水号唯一且不能重复
     */
    private int sfTransMessageIdCountLength = 4;

    public String getSfTransMessageId() {
        String key = "counter_SfTransMessageId";

        Long count = 1L;
        Long value = redisTemplateLong.opsForValue().get(key);

        if (value == null) {
            counter = new RedisAtomicLong(key, redisTemplateLong.getConnectionFactory(), count);
            counter.expireAt(dateUtil.getDay(1));
            sfTransMessage_currentDay = dateUtil.getCurrentDayStr("yyyyMMddHHmmss");

        } else {
            if (counter == null) {
                counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
                counter.set(value);
                counter.expireAt(dateUtil.getDay(1));
                sfTransMessage_currentDay = dateUtil.getCurrentDayStr("yyyyMMddHHmmss");
            }

            count = counter.incrementAndGet();
        }


        String countStr = String.valueOf(count);

        int minusLength = sfTransMessageIdCountLength - countStr.length();
        while (minusLength > 0) {
            countStr = "0" + countStr;
            --minusLength;
        }

        String sfTransMessageId = sfTransMessage_currentDay + countStr;

        logger.info("generate getSfTransMessageId:" + sfTransMessageId);

        return sfTransMessageId;
    }

    public Map<String, List<Object>> queryBySql(String sql, Class[] clazzs) {
        Map<String, List<Object>> result = new HashMap<>();

        int startPosition = 0;
        List<Object[]> values = (List<Object[]>) sessionFactory.getCurrentSession().createSQLQuery(sql).list();
        for (Class clazz : clazzs) {
            result.put(clazz.getName(), getValues(values, clazz, startPosition));

            startPosition += getColumnNum(clazz);
        }

        return result;
    }

    public List<Object> getValues(List<Object[]> values, Class clazz, int startPosition) {
        List<Object> objects = new ArrayList<>();

        // 设置对象
        Object obj = null;
        for (Object[] value : values) {
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            objects.add(setObjectValue(obj, value, startPosition));
        }

        return objects;
    }

    public Object setObjectValue(Object object, Object[] values, int startPosition) {

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (values[startPosition] != null) {
                boolean isSet = setFieldValue(field, values[startPosition], object);
                if (isSet) startPosition++;

            } else {
                startPosition++;
            }

            if (startPosition > values.length -1) {
                break;
            }

        }

        return object;
    }

    public int getColumnNum(Class clazz) {
        int num = 0;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) ||
                    field.isAnnotationPresent(JoinColumn.class)){
                num++;
            }
        }

        return num;
    }

    public String[] getSqlPart(String sql, Class clazz){
        String[] sqlPartArr = new String[4];

        String[] sqlParts = sql.split(" from ");
        sqlPartArr[0] = getSelectColumns("t", clazz);
        String[] sqlParts1 = sqlParts[1].split(" where ");

        sqlPartArr[1] = sqlParts1[0];
        if (sqlParts1.length == 2) {
            String parts[] = sqlParts1[1].split(" order by ");

            sqlPartArr[2] = parts[0];
            if (parts.length == 2) {
                sqlPartArr[3] = parts[1];
            }
        } else {
            String parts[] = sqlParts1[0].split(" order by ");

            sqlPartArr[1] = parts[0];
            sqlPartArr[2] = "";
            if (parts.length == 2) {
                sqlPartArr[3] = parts[1];
            }
        }

        return sqlPartArr;
    }

    public String getSelectColumns(String abbrTableName, Class clazz) {
        String selectColumns = "";

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String column = null;
            if (field.isAnnotationPresent(Column.class)){
                column = field.getAnnotation(Column.class).name();

            } else if (field.isAnnotationPresent(JoinColumn.class)){
                column = field.getAnnotation(JoinColumn.class).name();
            }

            if (column != null) {
                selectColumns += abbrTableName + "." + column + " as " + abbrTableName + column + ", ";
            }
        }

        int length = selectColumns.length();
        return length > 0 ? selectColumns.substring(0, length-", ".length()) : selectColumns;
    }

    /**
     * 获取多个值
     * @param hashKey
     * @return
     */
    public List<Object> getValuesFromHash(String hashKey) {
        List<Object> values = new ArrayList<>();
        List<Object> nullValueKeys = new ArrayList<>();

        BoundHashOperations hashOps = redisTemplate.boundHashOps(hashKey);

        Set keys = hashOps.keys();
        for (Object key : keys) {
            Object value = getFromRedis((String) key);

            if (value != null) {
                values.add(value);
            } else {
                nullValueKeys.add(key);
            }
        }

        if (nullValueKeys.size() == keys.size()) {
            deleteFromRedis(hashKey);

        } else {
            for (Object nullValueKey : nullValueKeys) {
                hashOps.delete(nullValueKey);
            }
        }

        return  values;
    }

    /**
     * 存入 key 到 hash
     * @param key
     * @return
     */
    public void putKeyToHash(String hashKey, Object key) {
        redisTemplate.boundHashOps(hashKey).put(key, null);
    }
}
