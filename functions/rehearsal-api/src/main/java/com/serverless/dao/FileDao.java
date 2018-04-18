package com.serverless.dao;

import com.serverless.config.DriverConfig;
import com.serverless.model.domain.File;
import com.serverless.sql.FileSQL;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class FileDao {
    private Connection connection;

    public FileDao() {
        DriverConfig driverConfig = new DriverConfig();
        try {
            this.connection = driverConfig.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void create(File file) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(FileSQL.CREATE_FILES);
        try {
            statement.setString(1, file.getName());
            statement.setLong(2, file.getSize());
            statement.setDate(3, new Date(file.getCreatedAt().toEpochSecond(ZoneOffset.UTC)));
            statement.setDate(4, new Date(file.getUpdatedAt().toEpochSecond(ZoneOffset.UTC)));
            statement.setLong(5, file.getUserId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
            statement.close();
        }
    }

    public Optional<List<File>> find(long userId) {
        try {
            Statement statement = connection.createStatement();
            List<File> files = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery(FileSQL.FIND_FILES_BY_USER_ID + userId);
            while (resultSet.next()) {
                File file = new File();
                file.setId(resultSet.getLong("id"));
                file.setName(resultSet.getString("name"));
                file.setSize(resultSet.getLong("size"));
                file.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                file.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                file.setUserId(resultSet.getLong("user_id"));
                files.add(file);
            }

            return Optional.of(files);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
