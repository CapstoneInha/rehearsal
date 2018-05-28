package com.serverless.utility.template;

import com.serverless.utility.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcTemplate {
    private Connection connection;

    public JdbcTemplate() throws SQLException {
        this.connection = DriverManager.getConnection(Constants.JDBC_URL, Constants.USER_NAME, Constants.PASSWORD);
        this.connection.setAutoCommit(false);
    }

    public void batchUpdate() throws SQLException {
        this.connection.commit();
    }

    public <T> long create(String sql, T object) {
        Class clazz = object.getClass();
        Field[] fileds = clazz.getDeclaredFields();
        try {
            for (Field field : fileds) {
                System.out.println("field : " + field.getName());
                Method method = clazz.getMethod("get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1));
                if(field.getType().isPrimitive()) {
                    sql = sql.replaceAll(":" + field.getName(), String.valueOf(method.invoke(object)));
                } else {
                    sql = sql.replaceAll(":" + field.getName(), StringUtils.wrapIfMissing(String.valueOf(method.invoke(object)), "\""));
                }
            }

            System.out.println(sql);

            ResultSet resultSet;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) return resultSet.getLong(1);
        } catch (SQLException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return -1L;
    }

    public <T> void update(String sql, T object) {
        Class clazz = object.getClass();
        Field[] fileds = clazz.getDeclaredFields();
        try {
            for (Field field : fileds) {
                System.out.println("field : " + field.getName());
                Method method = clazz.getMethod("get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1));
                if(field.getType().isPrimitive()) {
                    sql = sql.replaceAll(":" + field.getName(), String.valueOf(method.invoke(object)));
                } else {
                    sql = sql.replaceAll(":" + field.getName(), StringUtils.wrapIfMissing(String.valueOf(method.invoke(object)), "\""));
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public <T> LinkedList<T> find(String sql, Map<String, Object> param, Class<T> resultType) throws SQLException {
        for (String key : param.keySet()) {
            sql = sql.replaceAll(":" + key, param.get(key).toString());
        }
        System.out.println(sql);

        Field[] fileds = resultType.getDeclaredFields();
        LinkedList<Triple<String, String, Class>> fieldList = new LinkedList<>();
        for (Field field : fileds) {
            String schema = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(field.getName()))
                    .map(StringUtils::lowerCase)
                    .collect(Collectors.joining("_"));

            char firstChar = field.getName().charAt(0);
            String setMethodName = field.getName().replaceFirst(String.valueOf(firstChar), "set" + Character.toUpperCase(firstChar));
            Class type = field.getType();
            fieldList.add(Triple.of(schema, setMethodName, type));
        }

        Statement statement = null;
        ResultSet resultSet = null;
        LinkedList<T> resultList = new LinkedList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                T object = resultType.newInstance();
                for (Triple<String, String, Class> field : fieldList) {
                    Method method = resultType.getDeclaredMethod(field.getMiddle(), field.getRight());
                    method.invoke(object, resultSet.getObject(field.getLeft(), field.getRight()));
                }

                resultList.add(object);
            }

            return resultList;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        return resultList;
    }

    public boolean close() {
        if (connection == null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

}
