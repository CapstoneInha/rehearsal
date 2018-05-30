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

    public Optional<List<File>> find(long projectId, String eventType) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Map<String, Object> params = new ConcurrentHashMap<>();
        params.put("projectId", projectId);
        params.put("eventType", eventType);
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

}
