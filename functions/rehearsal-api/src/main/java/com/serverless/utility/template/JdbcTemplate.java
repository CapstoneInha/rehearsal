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
    private static JdbcTemplate jdbcTemplate;

    private JdbcTemplate() {
    }

    public static JdbcTemplate createJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate();
        }

        return jdbcTemplate;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.JDBC_URL, Constants.USER_NAME, Constants.PASSWORD);
    }

    public <T> long create(Connection connection, String sql, T object) {
        Class clazz = object.getClass();
        Field[] fileds = clazz.getDeclaredFields();
        try {
            for (Field field : fileds) {
                sql.replaceAll(":" + field.getName(), field.get(object).toString());
            }

            ResultSet resultSet;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return -1L;
    }

    public <T> LinkedList<T> find(Connection connection, String sql, Map<String, Object> param, Class<T> resultType) throws SQLException {
        param.forEach((key, value) -> {
            sql.replaceAll(":" + key, value.toString());
        });

        Class clazz = resultType.getClass();
        Field[] fileds = clazz.getDeclaredFields();
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
                    Method method = clazz.getDeclaredMethod(field.getMiddle(), field.getRight());
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

    public boolean close(Connection connection) {
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
