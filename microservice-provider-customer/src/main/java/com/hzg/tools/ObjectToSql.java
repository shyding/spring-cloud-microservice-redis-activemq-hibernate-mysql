package com.hzg.tools;

import com.google.gson.reflect.TypeToken;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import javax.persistence.*;

@Component
public class ObjectToSql {
    private Logger logger = Logger.getLogger(ObjectToSql.class);

    public String getMethodPerfix = "get";
    public  String setMethodPerfix = "set";

    @Autowired
    private Writer writer;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private Des des;

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
        List<List<Object>> manyToManyTableInfos = getManyToManyTableInfos(objectClass, object);
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

        List<List<Object>> oneToManyTableInfos = getOneToManyTableInfos(objectClass, object);
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

        selectSql += " order by t.id desc ";

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

    public String generateSuggestSqlByAnnotation(Object object, Field[] limitFields){
        Class objectClass = object.getClass();

        String suggestSql = "select t.* from " + getTableName(objectClass) + " t ";
        List<List<String>> columnValues = getColumnValues(objectClass, object);
        String where = "", limitWhere = "";

        for (List<String> columnValue : columnValues) {
            boolean isLimitColumn = false;
            if (limitFields != null) {
                for (Field field : limitFields) {
                    if (getColumn(field).equals(columnValue.get(0))) {
                        isLimitColumn = true;
                        break;
                    }
                }
            }

            if (!isLimitColumn) {
                if (columnValue.get(1).indexOf("'") == 0 && (columnValue.get(1).lastIndexOf("'") == columnValue.get(1).length() - 1)) {
                    where += columnValue.get(0) + " like '%" + columnValue.get(1).substring(1, columnValue.get(1).length() - 1) + "%' or ";

                } else if (!columnValue.get(1).contains("'")) {
                    where += columnValue.get(0) + " = " + columnValue.get(1) + " or ";
                }

            } else {
                limitWhere += columnValue.get(0) + " = " + columnValue.get(1) + " and ";
            }
        }

        if (where.length() > 0) {
            where = where.substring(0, where.length()-" or ".length());
        }

        if (limitWhere.length() > 0) {
            limitWhere = limitWhere.substring(0, limitWhere.length()-" and ".length());
        }

        if (where.length() > 0 && limitWhere.length() > 0) {
            suggestSql = suggestSql + " where " + " (" + where + ") and " + limitWhere;

        } else if (where.length() > 0 && limitWhere.length() == 0) {
            suggestSql = suggestSql + " where " + where;

        } else if (where.length() == 0 && limitWhere.length() > 0) {
            suggestSql = suggestSql + " where " + limitWhere;
        }

        suggestSql = suggestSql + " limit 30";
        logger.info("suggestSql: " + suggestSql);

        return suggestSql;
    }

    public String generateComplexSqlByAnnotation(Class clazz, Map<String, String> queryParameters, int position, int rowNum){
        String selectSql = "select t.* ", fromPart = getTableName(clazz)+" t, ", wherePart = "";

        List<List<String>> columnValues = new ArrayList<>();
        List<List<Object>> manyToManyTableInfos = new ArrayList<>();
        List<List<Object>> oneToManyTableInfos = new ArrayList<>();

        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(entry.getKey());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (field != null) {
                if (field.isAnnotationPresent(Column.class) ||
                        field.isAnnotationPresent(ManyToOne.class) ||
                        field.isAnnotationPresent(OneToOne.class)) {
                    List<String> columnValue = getColumnValue(field, entry.getValue());
                    if (columnValue != null) {
                        columnValues.add(columnValue);
                    }

                } else if (field.isAnnotationPresent(ManyToMany.class)) {
                    List<Object> columnValue = getManyToManyTableInfo(field, entry.getValue());
                    if (columnValue != null) {
                        manyToManyTableInfos.add(columnValue);
                    }

                } else if (field.isAnnotationPresent(OneToMany.class)) {
                    List<Object> columnValue = getOneToManyTableInfo(field, entry.getValue());
                    if (columnValue != null) {
                        oneToManyTableInfos.add(columnValue);
                    }
                }
            }
        }

        for (List<String> columnValue : columnValues) {
            /**
             * 字段含有 date 表示是日期字段，columnValue.get(1)的值如：2017/04/26 - 2017/04/27
             */
            if (columnValue.get(0).toLowerCase().contains("date") && columnValue.get(1).contains(" - ")) {
                String[] dateRange = columnValue.get(1).replace("/", "-").split(" - ");

                String startDate = dateRange[0].substring(1);
                String endDate = dateRange[1].substring(0, dateRange[1].length()-1);

                wherePart += "t." + columnValue.get(0) + " >= '" + startDate + "' and t." + columnValue.get(0) + " <= '" + dateUtil.getDaysDate(endDate, "yyyy-MM-dd", 1) + "' and ";

            } else {
                /**
                 * 含有 " in (" 的值如：{"id": " in (1, 2, 3)"} 或者  {"id": " not in (1, 2, 3)"}
                 */
                if (Pattern.compile("in\\s*\\(").matcher(columnValue.get(1)).find()) {
                    wherePart += "t." + columnValue.get(0) + " " + columnValue.get(1).substring(1) + " and ";

                    /**
                     * 值为数子
                     */
                } else if (columnValue.get(1).indexOf("'") == -1) {
                    wherePart += "t." + columnValue.get(0) + " = " + columnValue.get(1) + " and ";

                    /**
                     * 值为字符串
                     */
                } else {
                    wherePart += "t." + columnValue.get(0) + " like '%" + columnValue.get(1).substring(1, columnValue.get(1).length() - 1) + "%' and ";
                }
            }
        }

        int i = 1;
        if (manyToManyTableInfos.size() > 0) {
            for (List<Object> manyToManyTableInfo : manyToManyTableInfos) {
                String joinTableNickName = "t" + (i++), secondTableNickName = "t" + (i++);

                fromPart += manyToManyTableInfo.get(1).toString() + " " + joinTableNickName + ", " +
                        manyToManyTableInfo.get(4).toString() + " " + secondTableNickName + ", ";

                wherePart += "t." + manyToManyTableInfo.get(0).toString() + " = " + joinTableNickName + "." + manyToManyTableInfo.get(2).toString() + " and " +
                        joinTableNickName + "." + manyToManyTableInfo.get(3).toString() + " = " + secondTableNickName + "." + manyToManyTableInfo.get(5).toString() + " and ";

                wherePart = setWhereByValues(wherePart, secondTableNickName, (String)manyToManyTableInfo.get(6), (Class)manyToManyTableInfo.get(7));
            }
        }

        if (oneToManyTableInfos.size() > 0) {
            for (List<Object> oneToManyTableInfo : oneToManyTableInfos) {
                String joinTableNickName = "t" + (i++);

                fromPart += oneToManyTableInfo.get(1).toString() + " " + joinTableNickName + ", ";
                wherePart += "t." + oneToManyTableInfo.get(0).toString() + " = " + joinTableNickName + "." + oneToManyTableInfo.get(2).toString() + " and ";
                wherePart = setWhereByValues(wherePart, joinTableNickName, (String)oneToManyTableInfo.get(3), (Class)oneToManyTableInfo.get(4));
            }
        }

        if (fromPart.length() > ", ".length()) {
            selectSql += " from " + fromPart.substring(0, fromPart.length()-", ".length());
        }

        if (wherePart.length() > " and ".length()) {
            selectSql += " where " + wherePart.substring(0, wherePart.length()-" and ".length()) + " order by t.id desc ";
        } else {
            selectSql += " order by t.id desc ";
        }

        if (rowNum != -1) {
            selectSql += " limit " +position + "," + rowNum;
        }

        logger.info("selectSql:" + selectSql);

        return selectSql;
    }

    private String setWhereByValues(String wherePart, String joinTableNickName, String jsonValue, Class clazz) {
        Map<String, String> columnSumValues = getPropertySumValues(jsonValue, clazz);

        for (Map.Entry<String, String> entry : columnSumValues.entrySet()) {
            if (entry.getKey().toLowerCase().contains("date") && entry.getValue().contains(" - ")) {

                String[] dateRange = entry.getValue().replace("/", "-").split(" - ");
                wherePart += joinTableNickName + "." + entry.getKey() + " >= " + dateRange[0] + "' and " +
                        joinTableNickName + "." + entry.getKey() + " <= '" + dateRange[1] + " and ";

            } else {
                wherePart += joinTableNickName + "." + entry.getKey() + " in (" + entry.getValue() + ") and ";
            }
        }

        return wherePart;
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
            List<String> columnValue = getColumnValue(field, getFieldValue(object, field));
            if (columnValue != null) {
                columnValues.add(columnValue);
            }
        }

        return columnValues;
    }

    /**
     * 获取单个字段信息
     * @param field
     * @param value
     * @return
     */
    public List<String> getColumnValue(Field field, Object value) {
        List<String> columnValue = null;

        // 检查类中属性是否含有 column 注解
        String column = getColumn(field);

        if (value != null && !String.valueOf(value).trim().equals("") && column != null) {
            columnValue = new ArrayList<>();
            columnValue.add(column);
            columnValue.add(getValue(field, value));
        }

        return columnValue;
    }

    /**
     * 获取类属性对应的数据库表 字段名
     * @param field
     * @return
     */
    public String getColumn(Field field) {
        String column = null;

        if (field.isAnnotationPresent(Column.class)) {
            column = field.getAnnotation(Column.class).name();

        }else if(field.isAnnotationPresent(ManyToOne.class) ||
                field.isAnnotationPresent(OneToOne.class)){
            column = field.getAnnotation(JoinColumn.class).name();
        }

        return column;
    }

    /**
     * 获取所有属性域信息
     * @param clazz
     * @return
     */
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
     * 获取 ManyToMany 信息
     * @param clazz
     * @param object
     * @return
     */
    public List<List<Object>> getManyToManyTableInfos(Class clazz, Object object) {
        List<List<Object>> columnValues = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ManyToMany.class)) {

                List<Object> columnValue = getManyToManyTableInfo(field, getFieldValue(object, field));
                if (columnValue != null) {
                    columnValues.add(columnValue);
                }
            }

        }

        return columnValues;
    }

    /**
     * 获取单个域 ManyToMany 信息
     * @param field
     * @param value
     * @return
     */
    public List<Object> getManyToManyTableInfo(Field field, Object value) {
        List<Object> columnValue = null;

        // ManyToMany 注解
        String[] joinTableInfo = getJoinTableInfo(field);

        if (joinTableInfo != null && value != null) {
            columnValue = new ArrayList<>();

            //ManyToMany  关联表里的 tableName 信息
            columnValue.add("id"); // 主表 id
            columnValue.add(joinTableInfo[0]); // 连接表表名
            columnValue.add(joinTableInfo[1]); // 连接主表 id 的表名
            columnValue.add(joinTableInfo[2]); // 连接次表 id 的表名

            columnValue.add(getTableName((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0])); // 次表
            columnValue.add("id"); // 次表 id
            columnValue.add(value); // 次表对象值
            columnValue.add(((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]); // 次表 clazz 类

        }

        return columnValue;
    }

    /**
     * 获取 ManyToMany 连接表信息
     * @param field
     * @return
     */
    public String[] getJoinTableInfo(Field field) {
        String[] joinTableInfo = null;

        if(field.isAnnotationPresent(ManyToMany.class)){
            JoinTable joinTable = field.getAnnotation(JoinTable.class);

            joinTableInfo = new String[3];
            joinTableInfo[0] = joinTable.name();
            joinTableInfo[1] = joinTable.joinColumns()[0].name();
            joinTableInfo[2] = joinTable.inverseJoinColumns()[0].name();
        }

        return joinTableInfo;
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
     * 获取 json 里属性值的集合
     * @param json
     * @param clazz
     * @return
     */
    public Map<String, String> getPropertySumValues(String json, Class clazz) {
        Map<String, String> columnSumValues = new HashMap<>();

        List<Map<String, String>> jsonMaps = writer.gson.fromJson(json, new TypeToken<List<Map<String, String>>>(){}.getType());
        for (Map<String, String> jsonMap : jsonMaps) {
            for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                Field field = null;

                try {
                    field = clazz.getDeclaredField(entry.getKey());
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }

                if (field != null) {
                    List<String> columnValue = getColumnValue(field, entry.getValue());

                    if (columnValue != null) {
                        if (columnSumValues.containsKey(columnValue.get(0))) {
                            columnSumValues.put(columnValue.get(0), columnSumValues.get(columnValue.get(0)) + "," + columnValue.get(1));
                        } else {
                            columnSumValues.put(columnValue.get(0), columnValue.get(1));
                        }
                    }
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
    public List<List<Object>> getOneToManyTableInfos(Class clazz, Object object) {
        List<List<Object>> columnValues = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class)) {

                List<Object> columnValue = getOneToManyTableInfo(field, getFieldValue(object, field));
                if (columnValue != null) {
                    columnValues.add(columnValue);
                }
            }

        }

        return columnValues;
    }

    /**
     * 获取单个域 OneToMany 表信息
     * @param field
     * @param value
     * @return
     */
    public List<Object> getOneToManyTableInfo(Field field, Object value) {
        List<Object> columnValue = null;

        if(field.isAnnotationPresent(OneToMany.class)  && value != null){
            columnValue = new ArrayList<>();

            Class clazz = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];

            String joinColumn = "";
            Field[] fields = clazz.getDeclaredFields();
            for (Field field1 : fields) {
                if (field1.getName().equals(field.getAnnotation(OneToMany.class).mappedBy())) {
                    joinColumn = field1.getAnnotation(JoinColumn.class).name();
                    break;
                }
            }

            //OneToMany 关联表里的 tableName 信息
            columnValue.add("id"); // 主表 id
            columnValue.add(getTableName((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0])); // 次表
            columnValue.add(joinColumn); // 次表连接主表 id 的字段名
            columnValue.add(value); // 次表对象值
            columnValue.add(clazz); // 次表 class 类
        }

        return columnValue;
    }

    /**
     * 获取属性值
     * @param object
     * @param field
     * @return
     */
    public Object getFieldValue(Object object, Field field) {
        String fieldName = field.getName();
        String methodName = getMethodPerfix + fieldName.substring(0,1).toUpperCase() +
                fieldName.substring(1);
        Object value = null;

        try {
            value = object.getClass().getMethod(methodName).invoke(object);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return value;
    }

    /**
     * 根据对象产生 sql 语句
     * @param object
     * @param where
     * @return
     */
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

        if (field.isAnnotationPresent(Type.class)) {
            try {
                Class type = Class.forName(field.getAnnotation(Type.class).type());
                Object typeObj = type.newInstance();

                String returnType = (String) type.getMethod("returnedClassStr").invoke(typeObj);

                if (returnType.equals("Integer[]")) {
                    valueStr += "'" +  Arrays.deepToString((Integer[])value)  + "'";

                } else if (returnType.equals("FloatDesType")) {
                    valueStr += "'" + des.encrypt(String.valueOf(value)) + "'";

                } else if (returnType.equals("StringDesType")) {
                    valueStr += "'" + des.encrypt(String.valueOf(value)) + "'";
                }

            } catch (Exception e){}

        } else {
            if (field.getType().getSimpleName().equals("String")) {
                valueStr += "'" + value.toString() + "'";

            } else if (field.getType().getSimpleName().equals("Integer") ||
                    field.getType().getSimpleName().equals("int")) {
                valueStr += String.valueOf(value);

            } else if (field.getType().getSimpleName().equals("Double") ||
                    field.getType().getSimpleName().equals("double")) {
                valueStr += String.valueOf(value);

            } else if (field.getType().getSimpleName().equals("Float") ||
                    field.getType().getSimpleName().equals("float")) {
                valueStr += String.valueOf(value);

            } else {
                try {
                    valueStr += field.getType().getMethod("getId").invoke(value);
                } catch (Exception e) {
                    /**
                     * 含有 " in (" 的值如：{"id": " in (1, 2, 3)"} 或者  {"id": " not in (1, 2, 3)"}
                     */
                    if (Pattern.compile("in\\s*\\(").matcher(value.toString()).find()) {
                        valueStr += value.toString();
                    } else {
                        valueStr += "'" + value.toString() + "'";
                    }
                }
            }
        }

        return valueStr;
    }
}