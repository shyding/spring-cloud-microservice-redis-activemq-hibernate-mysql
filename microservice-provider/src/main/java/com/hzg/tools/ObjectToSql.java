package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Administrator on 2017/3/24.
 */
@Component
public class ObjectToSql {
    private Logger logger = Logger.getLogger(ObjectToSql.class);

    private String getMethodPerfix = "get";

    public String generateUpdateSql(Object object, String where){
        Class objectClass = object.getClass();

        String updateSql = "update " + objectClass.getSimpleName() + " set ";

        Field[] fields = objectClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                                fieldName.substring(1);
            Object value = null;

            try {
                value = objectClass.getMethod(methodName).invoke(object);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            if (value != null && !String.valueOf(value).trim().equals("")) {
                updateSql += fieldName + "=";
                updateSql = setValue(fields[i], value, updateSql);
                updateSql += ",";
            }
        }

        updateSql = updateSql.substring(0, updateSql.length()-1) + " where " + where;

        logger.info("updateSql:" + updateSql);

        return updateSql;
    }

    public String generateUpdateSqlByAnnotation(Object object, String where){
        Class objectClass = object.getClass();

        String tableName = "";
        if (objectClass.isAnnotationPresent(Entity.class)) {
            tableName = ((Entity)objectClass.getAnnotation(Entity.class)).name();
        }

        if (tableName.trim().equals("")) {
            return "";
        }

        String updateSql = "update " + tableName + " set ";

        Field[] fields = objectClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                    fieldName.substring(1);
            Object value = null;

            try {
                value = objectClass.getMethod(methodName).invoke(object);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            // 检查类中属性是否含有 column 注解
            String column = "";
            if (fields[i].isAnnotationPresent(Column.class)) {
                // 获取注解
                column = fields[i].getAnnotation(Column.class).name();
            }

            if (value != null && !String.valueOf(value).trim().equals("") && !column.trim().equals("")) {
                updateSql += column + "=";
                updateSql = setValue(fields[i], value, updateSql);
                updateSql += ",";
            }
        }

        updateSql = updateSql.substring(0, updateSql.length()-1) + " where " + where;

        logger.info("updateSql:" + updateSql);

        return updateSql;
    }

    public String setValue(Field field, Object value, String updateSql) {
        if (field.getType().getSimpleName().equals("String")) {
            updateSql += "'" + value.toString() + "'";

        } else if (field.getType().getSimpleName().equals("Integer") ||
                field.getType().getSimpleName().equals("int")) {
            updateSql += (Integer)value;

        } else if (field.getType().getSimpleName().equals("Double") ||
                field.getType().getSimpleName().equals("double")){
            updateSql += (Double)value;

        } else if (field.getType().getSimpleName().equals("Float") ||
                field.getType().getSimpleName().equals("float")){
            updateSql += (Float)value;

        } else {
            updateSql += "'" + String.valueOf(value) + "'";
        }

        return updateSql;
    }
}