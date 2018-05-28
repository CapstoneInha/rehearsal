package com.serverless.dao;

import com.serverless.model.domain.File;
import com.serverless.sql.FileSQL;
import com.serverless.utility.template.JdbcTemplate;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FileDao {
    private static FileDao fileDao;

    private FileDao() {
    }

    public static FileDao createFileDao() {
        if (fileDao == null) {
            fileDao = new FileDao();
        }

        return fileDao;
    }


    public long create(File file) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        long fileId = jdbcTemplate.create(FileSQL.CREATE_FILES, file);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return fileId;
    }

    public Optional<List<File>> find(long projectId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Map<String, Object> params = new ConcurrentHashMap<>();
        params.put("projectId", projectId);
        LinkedList<File> files = jdbcTemplate.find(FileSQL.FIND_FILES_BY_PROJECT_ID, params, File.class);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return Optional.ofNullable(files);
    }

    public Optional<File> findOne(long fileId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Map<String, Object> params = Collections.synchronizedMap(new LinkedHashMap<>());
        LinkedList<File> files = jdbcTemplate.find(FileSQL.FIND_FILE_BY_ID, params, File.class);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return Optional.ofNullable(files.getFirst());
    }

//    public Optional<File> findLastOne(Connection connection, long projectId) {
//        try {
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(StringUtils.replace(FileSQL.FIND_LAST_FILE_BY_PROJECT_ID, "#id", String.valueOf(projectId)));
//            File file = null;
//            if (resultSet.next()) {
//                file = new File();
//                file.setId(resultSet.getLong("id"));
//                file.setName(resultSet.getString("name"));
//                file.setSize(resultSet.getLong("size"));
//                file.setMediaType(MediaType.valueOf(resultSet.getString("media_type")));
//                file.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
//                file.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
//                file.setMemberId(resultSet.getLong("member_id"));
//            }
//            resultSet.close();
//            statement.close();
//            connection.close();
//            return Optional.of(file);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return Optional.empty();
//    }

}
