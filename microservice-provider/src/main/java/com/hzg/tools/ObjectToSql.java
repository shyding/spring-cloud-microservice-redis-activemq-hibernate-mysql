package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.log4j.Logger;

import javax.persistence.*;

@Component
public class ObjectToSql {
    private Logger logger = Logger.getLogger(ObjectToSql.class);

    public String getMethodPerfix = "get";
    public  String setMethodPerfix = "set";

    public String generateSelectSqlByAnnotation(Object object){
        Class objectClass = object.getClass();
        String selectSql = "select t.* ", fromPart = getTableName(objectClass)+" t, ", wherePart = "";

        /*List<Map<String, String>> columnFields = getAllColumnFields(objectClass);
        for (Map<String, String> columnField : columnFields) {
            for (Map.Entry<String, String> entry : columnField.entrySet()) {
                selectSql += "t." + entry.getKey() + " as " + entry.getValue() + ", ";
            }
        }*/

        List<List<String>> columnValues = getColumnValues(objectClass, object);
        for (List<String> columnValue : columnValues) {
            wherePart += "t." + columnValue.get(0) + "=" + columnValue.get(1) + " and ";
        }

        int i = 1;
        List<List<Object>> manyToManyTableInfos = getManyToManyTableInfo(objectClass, object);
        if (manyToManyTableInfos.size() > 0) {
            for (List<Object> manyToManyTableInfo : manyToManyTableInfos) {
                String joinTableNickName = "t" + (i++), secondTableNickName = "t" + (i++);

                fromPart += manyToManyTableInfo.get(1).toString() + " " + joinTableNickName + ", " +
                            manyToManyTableInfo.get(4).toString() + " " + secondTableNickName + ", ";

                wherePart += "t." + manyToManyTableInfo.get(0).toString() + " = " + joinTableNickName + "." + manyToManyTableInfo.get(2).toString() + " and " +
                             joinTableNickName + "." + manyToManyTableInfo.get(3).toString() + " = " + secondTableNickName + "." + manyToManyTableInfo.get(5).toString() + " and ";

                Map<String, String> columnSumValues = getColumnSumValues((Set<Object>)manyToManyTableInfo.get(6));
                for (Map.Entry<String, String> entry : columnSumValues.entrySet()) {
                    wherePart += secondTableNickName + "." + entry.getKey() + " in (" + entry.getValue() + ") and ";
                }
            }
        }

        List<List<Object>> oneToManyTableInfos = getOneToManyTableInfo(objectClass, object);
        if (oneToManyTableInfos.size() > 0) {
            for (List<Object> oneToManyTableInfo : oneToManyTableInfos) {
                String joinTableNickName = "t" + (i++);

                fromPart += oneToManyTableInfo.get(1).toString() + " " + joinTableNickName + ", ";
                wherePart += "t." + oneToManyTableInfo.get(0).toString() + " = " + joinTableNickName + "." + oneToManyTableInfo.get(2).toString() + " and ";

                Map<String, String> columnSumValues = getColumnSumValues((Set<Object>)oneToManyTableInfo.get(3));
                for (Map.Entry<String, String> entry : columnSumValues.entrySet()) {
                    wherePart += joinTableNickName + "." + entry.getKey() + " in (" + entry.getValue() + ") and ";
                }
            }
        }

        /*if (selectSql.length() > ", ".length()) {
            selectSql =  selectSql.substring(0, selectSql.length()-", ".length());
        }*/

        if (fromPart.length() > ", ".length()) {
            selectSql += " from " + fromPart.substring(0, fromPart.length()-", ".length());
        }

        if (wherePart.length() > " and ".length()) {
            selectSql += " where " + wherePart.substring(0, wherePart.length()-" and ".length());
        }

        logger.info("selectSql:" + selectSql);

        return selectSql;
    }

    public String generateUpdateSqlByAnnotation(Object object, String where){
        Class objectClass = object.getClass();

        String updateSql = "update " + getTableName(objectClass) + " set ";
        List<List<String>> columnValues = getColumnValues(objectClass, object);
        for (List<String> columnValue : columnValues) {
            updateSql += columnValue.get(0) + "=" + columnValue.get(1) + ",";
        }

        updateSql = updateSql.substring(0, updateSql.length()-1) + " where " + where;
        logger.info("updateSql:" + updateSql);

        return updateSql;
    }

    public String generateSuggestSqlByAnnotation(Object object){
        Class objectClass = object.getClass();

        String suggestSql = "select t.* from " + getTableName(objectClass) + " t ";
        List<List<String>> columnValues = getColumnValues(objectClass, object);
        String where = "";
        for (List<String> columnValue : columnValues) {
            where += columnValue.get(0) + " like '%" + columnValue.get(1).substring(1, columnValue.get(1).length() - 1) + "%' or ";
        }

        if (where.length() > 0) {
            where = " where " + where.substring(0, where.length()-" or ".length());
        }

        suggestSql = suggestSql + where + " limit 30";
        logger.info("suggestSql: " + suggestSql);

        return suggestSql;
    }

    public String generateComplexSqlByAnnotation(Object object, int position, int rowNum){
        Class objectClass = object.getClass();

        String complexSql = "select t.* from " + getTableName(objectClass) + " t ";
        List<List<String>> columnValues = getColumnValues(objectClass, object);
        String where = "";

        for (List<String> columnValue : columnValues) {
            if (columnValue.get(0).toLowerCase().contains("date")) { //字段含有 date 表示是日期字段，columnValue.get(1)的值如：2017/04/26 - 2017/04/26
                String[] dateRange = columnValue.get(1).replace("/", "-").split(" - ");
                where += columnValue.get(0) + " >= " + dateRange[0] + "' and " + columnValue.get(0) + " <= '" + dateRange[1] + " and ";

            } else {
                where += columnValue.get(0) + " like '%" + columnValue.get(1).substring(1, columnValue.get(1).length() - 1) + "%' and ";
            }
        }

        if (where.length() > 0) {
            where = " where " + where.substring(0, where.length()-" and ".length());
        }

        complexSql = complexSql + where + " limit " + position + "," + rowNum;
        logger.info("complexSql: " + complexSql);

        return complexSql;
    }

    public String getTableName(Class clazz) {
        String tableName = "";
        if (clazz.isAnnotationPresent(Entity.class)) {
            tableName = ((Entity)clazz.getAnnotation(Entity.class)).name();
        }
        return tableName;
    }

    /**
     * 获取字段信息
     * @param clazz
     * @param object
     * @return
     */
    public List<List<String>> getColumnValues(Class clazz, Object object) {
        List<List<String>> columnValues = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                    fieldName.substring(1);
            Object value = null;

            try {
                value = clazz.getMethod(methodName).invoke(object);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            // 检查类中属性是否含有 column 注解
            String column = "";

            if (field.isAnnotationPresent(Column.class)) {
                column = field.getAnnotation(Column.class).name();

            }else if(field.isAnnotationPresent(ManyToOne.class) ||
                    field.isAnnotationPresent(OneToOne.class)){
                column = field.getAnnotation(JoinColumn.class).name();
            }

            if (value != null && !String.valueOf(value).trim().equals("") && !column.trim().equals("")) {
                List<String> columnValue = new ArrayList<>();

                columnValue.add(column);
                columnValue.add(getValue(field, value));

                columnValues.add(columnValue);

            }
        }

        return columnValues;
    }

    public List<Map<String, String>> getAllColumnFields(Class clazz) {
        List<Map<String, String>> columnFields = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Map<String, String> columnField = new HashMap<>();

            if (field.isAnnotationPresent(Column.class) ||
                    field.isAnnotationPresent(ManyToOne.class) ||
                    field.isAnnotationPresent(OneToOne.class)) {
                columnField.put(field.getAnnotation(Column.class).name(), field.getName());
            }

            columnFields.add(columnField);
        }

        return columnFields;
    }

    /**
     * 获ManyToMany信息
     * @param clazz
     * @param object
     * @return
     */
    List<List<Object>> getManyToManyTableInfo(Class clazz, Object object) {
        List<List<Object>> columnValues = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                    fieldName.substring(1);
            Object value = null;

            try {
                value = clazz.getMethod(methodName).invoke(object);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            // ManyToMany 注解
            String joinTableName = "", joinFirstIdColumn = "", joinSecondIdColumn = "";

            if(field.isAnnotationPresent(ManyToMany.class)){
                JoinTable joinTable = field.getAnnotation(JoinTable.class);

                joinTableName = joinTable.name();
                joinFirstIdColumn = joinTable.joinColumns()[0].name();
                joinSecondIdColumn = joinTable.inverseJoinColumns()[0].name();
            }

            if (!joinTableName.trim().equals("") && value != null) {
                Set<Object> objects = (Set<Object>)value;
                if (objects.size() > 0) {
                    List<Object> columnValue = new ArrayList<>();

                    //ManyToMany  关联表里的 tableName 信息
                    columnValue.add("id"); // 主表 id
                    columnValue.add(joinTableName); // 连接表表名
                    columnValue.add(joinFirstIdColumn); // 连接主表 id 的表名
                    columnValue.add(joinSecondIdColumn); // 连接次表 id 的表名

                    columnValue.add(getTableName(objects.toArray()[0].getClass())); // 次表
                    columnValue.add("id"); // 次表 id
                    columnValue.add(value); // 次表对象值

                    columnValues.add(columnValue);
                }
            }
        }

        return columnValues;
    }

    /**
     * 获取集合里列值的集合
     * @param objects
     * @return
     */
    public Map<String, String> getColumnSumValues(Set<Object> objects) {
        Map<String, String> columnSumValues = new HashMap<>();

        for (Object object : objects) {
            List<List<String>> columnValues = getColumnValues(object.getClass(), object);
            for (List<String> columnValue : columnValues) {
                String column = columnValue.get(0);

                if (columnSumValues.containsKey(column)) {
                    columnSumValues.put(column, columnSumValues.get(column) + "," + columnValue.get(1));
                } else {
                    columnSumValues.put(column, columnValue.get(1));
                }
            }
        }

        return columnSumValues;
    }

    /**
     * 获取 OneToMany 表信息
     * @param clazz
     * @param object
     * @return
     */
    List<List<Object>> getOneToManyTableInfo(Class clazz, Object object) {
        List<List<Object>> columnValues = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                    fieldName.substring(1);
            Object value = null;

            try {
                value = clazz.getMethod(methodName).invoke(object);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            if(field.isAnnotationPresent(OneToMany.class)  && value != null){
                Set<Object> objects = (Set<Object>)value;
                if (objects.size() > 0) {
                    List<Object> columnValue = new ArrayList<>();

                    //OneToMany 关联表里的 tableName 信息
                    columnValue.add("id"); // 主表 id
                    columnValue.add(getTableName(objects.toArray()[0].getClass())); // 次表
                    columnValue.add("id"); // 次表 id
                    columnValue.add(value); // 次表对象值

                    columnValues.add(columnValue);
                }
            }
        }

        return columnValues;
    }

    public String generateUpdateSql(Object object, String where){
        Class objectClass = object.getClass();

        String updateSql = "update " + objectClass.getSimpleName() + " set ";

        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
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
                updateSql += getValue(field, value);
                updateSql += ",";
            }
        }

        updateSql = updateSql.substring(0, updateSql.length()-1) + " where " + where;

        logger.info("updateSql:" + updateSql);

        return updateSql;
    }

    public String getValue(Field field, Object value) {
        String valueStr = "";

        if (field.getType().getSimpleName().equals("String")) {
            valueStr += "'" + value.toString() + "'";

        } else if (field.getType().getSimpleName().equals("Integer") ||
                field.getType().getSimpleName().equals("int")) {
            valueStr += (Integer)value;

        } else if (field.getType().getSimpleName().equals("Double") ||
                field.getType().getSimpleName().equals("double")){
            valueStr += (Double)value;

        } else if (field.getType().getSimpleName().equals("Float") ||
                field.getType().getSimpleName().equals("float")){
            valueStr += (Float)value;

        } else {
            try {
               valueStr += field.getType().getMethod("getId").invoke(value);
            } catch (Exception e) {
                logger.info(e.getMessage());

                valueStr += "'" + value.toString() + "'";
            }
        }

        return valueStr;
    }
}