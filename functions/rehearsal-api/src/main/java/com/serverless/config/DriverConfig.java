package com.serverless.config;

import com.serverless.utility.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverConfig {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.JDBC_URL,
                Constants.USER_NAME,Constants.PASSWORD);
    }

}
