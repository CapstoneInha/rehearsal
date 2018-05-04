package com.serverless.dao;

import com.serverless.config.DriverConfig;
import com.serverless.model.domain.File;
import com.serverless.sql.FileSQL;
import com.serverless.utility.enums.MediaType;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class FileDao {

    public FileDao() {
    }

    public long create(File file) throws SQLException {
        Connection connection = DriverConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(FileSQL.CREATE_FILES, Statement.RETURN_GENERATED_KEYS);
        ResultSet resultSet = null;
        try {
            preparedStatement.setString(1, file.getName());
            preparedStatement.setString(2, file.getMediaType().name());
            preparedStatement.setLong(3, file.getSize());
            preparedStatement.setDate(4, new Date(file.getCreatedAt().toEpochSecond(ZoneOffset.UTC)));
            preparedStatement.setDate(5, new Date(file.getUpdatedAt().toEpochSecond(ZoneOffset.UTC)));
            preparedStatement.setLong(6, file.getMemberId());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }

        return -1;
    }

    public Optional<List<File>> find(long userId) {
        try {
            Connection connection = DriverConfig.getConnection();
            Statement statement = connection.createStatement();
            List<File> files = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery(FileSQL.FIND_FILES_BY_PROJECT_ID + userId);
            while (resultSet.next()) {
                File file = new File();
                file.setId(resultSet.getLong("id"));
                file.setName(resultSet.getString("name"));
                file.setSize(resultSet.getLong("size"));
                file.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                file.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                file.setMemberId(resultSet.getLong("member_id"));
                files.add(file);
            }

            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(files);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<File> findOne(long fileId) {
        try {
            Connection connection = DriverConfig.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FileSQL.FIND_FILE_BY_ID + fileId);
            resultSet.next();
            File file = new File();
            file.setId(resultSet.getLong("id"));
            file.setName(resultSet.getString("name"));
            file.setSize(resultSet.getLong("size"));
            file.setMediaType(MediaType.valueOf(resultSet.getString("media_type")));
            file.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
            file.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
            file.setMemberId(resultSet.getLong("member_id"));

            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<File> findLastOne(long projectId) {
        try {
            Connection connection = DriverConfig.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(StringUtils.replace(FileSQL.FIND_LAST_FILE_BY_PROJECT_ID, "#id", String.valueOf(projectId)));
            File file = null;
            if(resultSet.next()) {
                file = new File();
                file.setId(resultSet.getLong("id"));
                file.setName(resultSet.getString("name"));
                file.setSize(resultSet.getLong("size"));
                file.setMediaType(MediaType.valueOf(resultSet.getString("media_type")));
                file.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                file.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                file.setMemberId(resultSet.getLong("member_id"));
            }
            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
