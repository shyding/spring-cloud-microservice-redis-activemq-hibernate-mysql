package com.hzg.tools;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

@Component
public class IntegersType implements UserType {

    protected static final int[] SQL_TYPES = { Types.VARCHAR };

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Integer[]) this.deepCopy(value);
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {

        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        if(resultSet.getArray(names[0]) == null){
            return new Integer[0];
        }

        return ((Writer) SpringUtil.getBean("writer")).gson.fromJson(names[0], Integer[].class);
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, SQL_TYPES[0]);
        } else {
            statement.setString(index, Arrays.deepToString((Integer[])value));
        }
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class<Integer[]> returnedClass() {
        return Integer[].class;
    }

    public String returnedClassStr() {
        return "Integer[]";
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }
}