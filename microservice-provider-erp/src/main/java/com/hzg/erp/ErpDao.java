package com.hzg.erp;

import com.hzg.base.Dao;
import com.hzg.tools.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ErpDao extends Dao {

    Logger logger = Logger.getLogger(ErpDao.class);

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
}
