package com.hzg.base;

/**
 * Created by Administrator on 2017/4/20.
 */

import com.hzg.tools.Des;
import com.hzg.tools.ObjectToSql;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class Dao {
    private Logger logger = Logger.getLogger(Dao.class);

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ObjectToSql objectToSql;

    @Autowired
    private Writer writer;

    @Autowired
    private Des des;

    /**
     * 保存对象
     * @param object
     * @return
     */
    public String save(Object object){
        sessionFactory.getCurrentSession().save(object);
        Class clazz = object.getClass();
        storeToRedis(clazz.getName() + "_" + getId(object, clazz), object);

        return "success";
    }


    /**
     * 删除对象
     * @param object
     * @return
     */
    public String delete(Object object) {
        Class clazz = object.getClass();
        Integer id = getId(object, clazz);

        int result = sessionFactory.getCurrentSession().createSQLQuery(
                "delete from " + objectToSql.getTableName(clazz) + " where id = " + id).executeUpdate();
        deleteFromRedis(clazz.getName() + "_" + id);

        return (result > 0 ? "success" : "fail") + "," + result + " item deleted";
    }




    /**
     * 更新表的部分字段，不会更新 “多对多表”，“一对多表” 关联表字段
     * 如：user 表的记录，post 表的记录是多对多关系，它们之间用 hzg_sys_userpost_relation表的
     * 记录(id, userId, postId)作为关联，用户改变所在岗位时即改变 user 表的记录里的 postId,
     * 不会同时更新 hzg_sys_userpost_relation 里的 postId
     *
     * @param id
     * @param object
     * @return
     */
    public String updateById(Integer id, Object object){
        //更新数据库记录后，同时重新设置redis缓存
        int result = sessionFactory.getCurrentSession().createSQLQuery(
                objectToSql.generateUpdateSqlByAnnotation(object, "id=" + id)).executeUpdate();
        updateObjectToRedis(object);

        return (result > 0 ? "success" : "fail") + "," + result + " item updated";
    }

    /**
     * 更新 redis 缓存的对象的值
     * @param object
     */
    public void updateObjectToRedis(Object object) {
        Class clazz = object.getClass();
        Integer id = getId(object, clazz);

        if (id != null) {
            String key = clazz.getName() + "_" + id;
            Object redisObject = getFromRedis(key);

            if (redisObject != null) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {

                    String partMethodName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                    String getMethodName = objectToSql.getMethodPerfix + partMethodName,
                            setMethodName = objectToSql.setMethodPerfix + partMethodName;

                    Object value = null;
                    try {
                        value = clazz.getMethod(getMethodName).invoke(object);
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }

                    try {
                        if (value != null) {
                            clazz.getMethod(setMethodName, field.getType()).invoke(redisObject, value);
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }

                storeToRedis(key, redisObject);

            } else {
                queryStoreObjectById(id, clazz);
            }
        }
    }


    /**
     * 更新 “多对多表”关联表字段
     * @param id
     * @param relateIds 关联id
     * @param unRelateIds 取消关联id
     * @param clazz
     * @return
     */
    public String updateRelateId(Integer id, List<Integer> relateIds, List<Integer> unRelateIds, Class clazz) {
        int result = 0;

        String[] relateTableInfo = getRelateTableInfo(clazz);
        String tableName = relateTableInfo[0], firstIdColumn = relateTableInfo[1], secondIdColumn = relateTableInfo[2];

        for (Integer relateId : relateIds) {
            if (!unRelateIds.contains(relateId)) {
                Object relate = sessionFactory.getCurrentSession().createSQLQuery(
                        "select * from " + tableName + " where " + firstIdColumn + " = " + id + " and " + secondIdColumn + " = " + relateId).uniqueResult();

                if (relate == null) {
                    result = sessionFactory.getCurrentSession().createSQLQuery(
                            "insert into " + tableName + "(" + firstIdColumn + "," + secondIdColumn + ") " +
                                    " values (" + id + "," + relateId + ")").executeUpdate();
                }
            }
        }

        for (Integer unRelateId : unRelateIds) {
            if (!relateIds.contains(unRelateId)) {
                result = sessionFactory.getCurrentSession().createSQLQuery(
                        "delete from " + tableName +
                                " where " +  firstIdColumn + "=" + id +
                                " and " + secondIdColumn + "=" + unRelateId).executeUpdate();
            }
        }

        return (result > 0 ? "success" : "fail") + "," + result + " item updated";
    }

    /**
     * 获取关联表信息
     * @param clazz
     * @return
     */
    public String[] getRelateTableInfo(Class clazz) {
        String joinTableName = "", joinFirstIdColumn = "", joinSecondIdColumn = "";
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                JoinTable joinTable = field.getAnnotation(JoinTable.class);
                joinTableName = joinTable.name();
                joinFirstIdColumn = joinTable.joinColumns()[0].name();
                joinSecondIdColumn = joinTable.inverseJoinColumns()[0].name();
            }
        }

        return new String[]{joinTableName, joinFirstIdColumn, joinSecondIdColumn};
    }




    /**
     * 有 id 则根据 id 查询，没有再根据其他属性查询
     * @param object
     * @return
     */
    public List query(Object object){
        List objects = new ArrayList<>();

        Class clazz = object.getClass();
        Integer id = getId(object, clazz);

        if (id != null) {
            Object dbObject = queryById(id, clazz);
            if (dbObject != null) {
                objects.add(dbObject);
            }

        }else {
            objects = queryBySql(objectToSql.generateSelectSqlByAnnotation(object), clazz);

            for (Object dbObject : objects) {
                querySetRelateObject(dbObject);
            }
        }

        return objects;
    }

    /**
     * 如果 redis 里没有缓存 object，则从数据库里查询 object，同时设置查询到的 object 到 redis。
     * 有的话，则重新设置对象里的关联对象值
     * @param id
     * @param clazz
     * @return
     */
    public Object queryById(Integer id, Class clazz){
        Object dbObject = getFromRedis(clazz.getName() + "_" + id);
        if (dbObject == null) {
            dbObject = queryStoreObjectById(id, clazz);

        } else {
            setRelateObject(dbObject);
        }

        return dbObject;
    }

    /**
     * 根据 sql 查询
     * @param sql
     * @param clazz
     * @return
     */
    public List<Object> queryBySql(String sql, Class clazz) {
        List<Object> objects = new ArrayList<>();

        List<Object[]> values = (List<Object[]>) sessionFactory.getCurrentSession().createSQLQuery(sql).list();

        // 设置对象
        Object obj = null;
        for (Object[] value : values) {
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            objects.add(setObjectValue(obj, value));
        }

        return objects;
    }

    /**
     * 根据 id 查询对象，设置对象的关联对象，然后把对象存储到缓存
     * @param id
     * @param clazz
     */
    public Object queryStoreObjectById(Integer id, Class clazz) {
        Object dbObject = null;
        Object[] objectValue =  (Object[])sessionFactory.getCurrentSession().createSQLQuery("select * from " + objectToSql.getTableName(clazz) + " where id = " + id).uniqueResult();

        if(objectValue != null) {
            try {
                dbObject = setObjectValue(clazz.newInstance(), objectValue);
            } catch (Exception e) {
                e.printStackTrace();
            }

            querySetRelateObject(dbObject);
            storeToRedis(clazz.getName() + "_" + id, dbObject);
        }

        return dbObject;
    }

    /**
     * 设置对象值
     * @param object
     * @param values
     * @return
     */
    public Object setObjectValue(Object object, Object[] values) {
        int i = 0;

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (values[i] != null) {
                boolean isSet = setFieldValue(field, values[i], object);
                if (isSet) i++;

            } else {
                i++;
            }

            if (i > values.length -1) {
                break;
            }

        }

        return object;
    }

    /**
     * 设置对象属性值
     * @param field
     * @param value
     * @param object
     * @return
     */
    public boolean setFieldValue(Field field, Object value, Object object) {
        boolean isSet = false;

        Class clazz = object.getClass();
        String fieldName = field.getName();
        String methodName = objectToSql.setMethodPerfix +
                fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        try {
            if (field.isAnnotationPresent(Column.class) ||
                    field.isAnnotationPresent(ManyToOne.class) ||
                    field.isAnnotationPresent(OneToOne.class)){

                if (field.getType().getName().equals(value.getClass().getName())) {
                    clazz.getMethod(methodName, field.getType()).invoke(object, value);

                } else {
                    Object parentValue = field.getType().newInstance();
                    field.getType().getMethod("setId", value.getClass()).invoke(parentValue, value);
                    clazz.getMethod(methodName, field.getType()).invoke(object, parentValue);
                }

                isSet = true;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());

            try {
                if (field.isAnnotationPresent(Type.class)){
                    Class type = Class.forName(field.getAnnotation(Type.class).type());
                    Object typeObj = type.newInstance();

                    String returnType = (String) type.getMethod("returnedClassStr").invoke(typeObj);

                    if (returnType.equals("Integer[]")) {
                        value = writer.gson.fromJson((String)value, Integer[].class);

                    } else if (returnType.equals("FloatDesType")) {
                        value = Float.parseFloat(des.decrypt((String)value));
                    }

                    clazz.getMethod(methodName, field.getType()).invoke(object, value);
                    isSet = true;
                }
            } catch (Exception e1) {
                logger.info(e1.getMessage());
            }
        }

        return isSet;
    }

    /**
     * 查询并设置关联对象
     * @param object
     */
    public void querySetRelateObject(Object object) {
        Class clazz = object.getClass();

        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                String partMethodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                if (field.isAnnotationPresent(ManyToOne.class) ||
                        field.isAnnotationPresent(OneToOne.class)) {
                    Object relateObject =  clazz.getMethod(objectToSql.getMethodPerfix + partMethodName).invoke(object);

                    if (relateObject != null) {
                        clazz.getMethod(objectToSql.setMethodPerfix + partMethodName, field.getType()).
                                invoke(object, queryBySql(objectToSql.generateSelectSqlByAnnotation(relateObject), relateObject.getClass()).get(0));
                    }
                }

                if (field.isAnnotationPresent(ManyToMany.class) ||
                        field.isAnnotationPresent(OneToMany.class)) {
                    Set<Object> relateObjects = (Set<Object>) clazz.getMethod(objectToSql.getMethodPerfix + partMethodName).invoke(object);
                    Set<Object> relateDbObjects = new HashSet<>();

                    if (relateObjects != null && relateObjects.size() > 0) {
                        for (Object relateObject : relateObjects) {
                            relateDbObjects.add(queryBySql(objectToSql.generateSelectSqlByAnnotation(relateObject), relateObject.getClass()).get(0));
                        }
                    } else {
                        relateDbObjects = queryOneOrManyToManyObjects(field, object);
                    }

                    clazz.getMethod(objectToSql.setMethodPerfix + partMethodName, field.getType()).invoke(object, relateDbObjects);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e);
        }
    }

    /**
     * redis 缓存中对象里的关联对象在实际环境中对应的记录值可能已经修改，
     * 但是 redis 缓存中对象里的关联对象的值还没有被修改，所以重新设置对象里的关联对象
     * @param dbObject
     * @throws Exception
     */
    public void setRelateObject(Object dbObject) {
        Class clazz = dbObject.getClass();

        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                String partMethodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                if (field.isAnnotationPresent(ManyToOne.class) ||
                        field.isAnnotationPresent(OneToOne.class)) {

                    Integer relateId = getId(clazz.getMethod(objectToSql.getMethodPerfix + partMethodName).invoke(dbObject), field.getType());

                    Object relateDbObject = null;
                    if (relateId != null) {
                        relateDbObject = getFromRedis(field.getType().getName() + "_" +  String.valueOf(relateId));

                        if (relateDbObject == null) {
                            relateDbObject = queryStoreObjectById(relateId, field.getType());
                        }

                    } else {
                        relateDbObject = queryOneOrManyToOneObject(field, dbObject);
                    }

                    clazz.getMethod(objectToSql.setMethodPerfix + partMethodName, field.getType()).invoke(dbObject, relateDbObject);
                }

                if (field.isAnnotationPresent(ManyToMany.class) ||
                        field.isAnnotationPresent(OneToMany.class)) {
                    Set<Object> relateObjects = (Set<Object>) clazz.getMethod(objectToSql.getMethodPerfix + partMethodName).invoke(dbObject);

                    if (relateObjects != null && relateObjects.size() > 0) {
                        Set<Object> relateDbObjects = new HashSet<>();

                        for (Object relateObject : relateObjects) {
                            Integer relateId = getId(relateObject, relateObject.getClass());

                            if (relateId != null) {
                                Object relateDbObject = getFromRedis(relateObject.getClass().getName() + "_" +  String.valueOf(relateId));

                                if (relateDbObject == null) {
                                    relateDbObject = queryStoreObjectById(relateId, relateObject.getClass());
                                }

                                relateDbObjects.add(relateDbObject);

                            } else {
                                relateDbObjects = queryOneOrManyToManyObjects(field, dbObject);
                                break;
                            }
                        }


                        relateObjects = relateDbObjects;

                    } else {
                        relateObjects = queryOneOrManyToManyObjects(field, dbObject);
                    }

                    clazz.getMethod(objectToSql.setMethodPerfix + partMethodName, field.getType()).invoke(dbObject, relateObjects);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    /**
     * 一对一，多对一的属性值为空时，就查询该属性值
     * @param field
     * @param object
     * @return
     */
    public Object queryOneOrManyToOneObject(Field field, Object object) {

        Object relateObject = null;

        List dbObjects = queryBySql(objectToSql.generateSelectSqlByAnnotation(object), object.getClass());
        if (dbObjects != null && dbObjects.size() > 0) {
            Object dbObject = dbObjects.get(0);

            String fieldName = field.getName();

            Field[] fields = dbObject.getClass().getDeclaredFields();
            for (Field field1 : fields) {
                if (field1.getName().equals(fieldName)) {

                    try {
                        relateObject = dbObject.getClass().getMethod(
                                objectToSql.getMethodPerfix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)).invoke(dbObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

        if (relateObject != null) {
            List fieldValues = query(relateObject);

            if (fieldValues != null && fieldValues.size() > 0) {
                relateObject = fieldValues.get(0);
            }
        }

        return relateObject;
    }

    /**
     * 多对多，一对多的属性值为空时，就查询这些属性值
     * sql 语句 select t.* 中把主表缩写 t 的  改为次表缩写 tn 就可以查询次表信息：select tn.*
     * @param field
     * @param object
     * @return
     */
    public Set<Object> queryOneOrManyToManyObjects(Field field, Object object) {

        Class fieldActualClazz = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
        String fieldTableName = objectToSql.getTableName(fieldActualClazz);

        Set<Object> relateObjects = new HashSet<>();
        try {
            relateObjects.add(fieldActualClazz.newInstance());
            object.getClass().getMethod(objectToSql.setMethodPerfix + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
                    field.getType()).invoke(object, relateObjects);
        } catch (Exception e){
            logger.info(e.getMessage());
            e.printStackTrace();
        }

        String selectSql = objectToSql.generateSelectSqlByAnnotation(object);
        int startPos = selectSql.indexOf(fieldTableName)+fieldTableName.length();
        String partSql = selectSql.substring(selectSql.indexOf(fieldTableName)+fieldTableName.length(),
                selectSql.indexOf(" where ", startPos)).trim();

        String fieldNickTableName = "t1";
        if (partSql.contains(",")) {
            fieldNickTableName = selectSql.substring(selectSql.indexOf(fieldTableName)+fieldTableName.length(),
                    selectSql.indexOf(",", startPos)).trim();
        } else {
            fieldNickTableName = partSql;
        }

        List fieldValues = queryBySql(selectSql.replace("t.*", fieldNickTableName+".*"), fieldActualClazz);
        relateObjects.clear();
        for (Object fieldValue : fieldValues) {
            relateObjects.add(fieldValue);
        }

        return relateObjects;
    }

    /**
     * 获取 id
     * @param object
     * @param clazz
     * @return
     */
    public Integer getId(Object object, Class clazz) {
        Integer id = null;

        if (object != null) {
            try {
                id = (Integer) clazz.getMethod("getId").invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return id;
    }



    /**
     * 建议、提示查询
     * @param object
     * @param limitFields
     * @return
     */
    public List suggest(Object object, Field[] limitFields){
        return queryBySql(objectToSql.generateSuggestSqlByAnnotation(object, limitFields), object.getClass());
    }

    /**
     * 复杂查询
     * @param clazz
     * @param queryParameters
     * @param position
     * @param rowNum
     * @return
     */
    public List complexQuery(Class clazz, Map<String, String> queryParameters, int position, int rowNum){
        List objects = queryBySql(objectToSql.generateComplexSqlByAnnotation(clazz, queryParameters, position, rowNum), clazz);
        for (Object dbObject : objects) {
            querySetRelateObject(dbObject);
        }
        return  objects;
    }

    /**
     * 查询记录数
     * @param clazz
     * @param queryParameters
     * @return
     */
    public BigInteger recordsSum(Class clazz, Map<String, String> queryParameters){
        String sql = objectToSql.generateComplexSqlByAnnotation(clazz, queryParameters, 0, -1);
        int pos = sql.indexOf(" order by ");
        if (pos != -1) {
            sql = sql.substring(0, pos);
        }
        sql = sql.replace("t.*", "count(t.id)");
        return (BigInteger)sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();
    }


    /**
     * 值是否重复
     * @param clazz
     * @param fieldName
     * @param value
     * @param id
     * @return
     */
    public boolean isValueRepeat(Class clazz, String fieldName, String value, Integer id) {
        boolean isRepeat = true;

        Object object = null;
        try {
            object = clazz.newInstance();
            Field field = clazz.getDeclaredField(fieldName);

            clazz.getMethod(objectToSql.setMethodPerfix + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
                    field.getType()).invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Object> objects = query(object);

        if (objects.size() == 0) {
            isRepeat = false;
        } else if (objects.size() == 1) {
            if (id == getId(objects.get(0), clazz)) {
                isRepeat = false;
            }
        }

        return isRepeat;
    }





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
