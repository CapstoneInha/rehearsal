package com.serverless.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DriverConfig {
    private HikariDataSource hikariDataSource;

    public DriverConfig() {
        HikariConfig config = new HikariConfig(
                this
                        .getClass()
                        .getClassLoader()
                        .getResource("app.properties")
                        .getFile());
        this.hikariDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

}
