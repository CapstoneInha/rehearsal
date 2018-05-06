package com.serverless.dao;

import com.serverless.config.DriverConfig;
import com.serverless.model.domain.File;
import com.serverless.sql.FileSQL;
import com.serverless.utility.enums.MediaType;
import com.serverless.utility.template.JdbcTemplate;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static com.serverless.utility.template.JdbcTemplate.createJdbcTemplate;


public class FileDao {
    private static FileDao fileDao;
    private JdbcTemplate jdbcTemplate;

    private FileDao() {
        jdbcTemplate = createJdbcTemplate();
    }

    public static FileDao createFileDao() {
        if (fileDao == null) {
            fileDao = new FileDao();
        }

        return fileDao;
    }


    public long create(File file) throws SQLException {
        Connection con = jdbcTemplate.getConnection();
        con.setAutoCommit(false);
        long fileId = jdbcTemplate.create(con, FileSQL.CREATE_FILES, File.class);
        con.commit();
        jdbcTemplate.close(con);
        return fileId;
    }

    public Optional<List<File>> find(long userId) throws SQLException {
        Connection con = jdbcTemplate.getConnection();
        con.setAutoCommit(false);
        Map<Object, Object> params = Collections.synchronizedMap(new LinkedHashMap<>());
        long fileId = jdbcTemplate.find(con, FileSQL.FIND_FILES_BY_PROJECT_ID, File.class);
        con.commit();
        jdbcTemplate.close(con);

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

    public Optional<File> findLastOne(Connection connection, long projectId) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(StringUtils.replace(FileSQL.FIND_LAST_FILE_BY_PROJECT_ID, "#id", String.valueOf(projectId)));
            File file = null;
            if (resultSet.next()) {
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
