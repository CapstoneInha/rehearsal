package com.serverless.service;

import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.model.dto.FileDto;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.template.S3Template;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.serverless.utility.template.JdbcTemplate.getConnection;

public class FileService {
    private FileDao fileDao;
    private HistoryDao historyDao;
    private static FileService fileService;

    private FileService() {
        fileDao = FileDao.createFileDao();
        historyDao = HistoryDao.createHistoryDao();
    }

    public static FileService createFileService() {
        if (fileService == null) {
            fileService = new FileService();
        }

        return fileService;
    }

    public void create(FileDto fileDto, Map<String, String> pathParams) {
        try {
            Connection connection = getConnection();
            connection.setAutoCommit(false);

            long memberId = Long.parseLong(pathParams.get("userId"));
            long projectId = Long.parseLong(pathParams.get("projectId"));

            long fileSize = upload(memberId + "/" + fileDto.getName(), fileDto.getData());
            File file = new File(fileDto);
            file.setSize(fileSize);
            file.setMemberId(memberId);
            long fileId = fileDao.create(connection, file);

            History history = new History();
            history.setFileId(fileId);
            history.setProjectId(projectId);
            history.setCreateAt(LocalDateTime.now());
            history.setEventType(EventType.CREATE_AUDIO);
            historyDao.create(connection, history);

            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Optional<List<File>> find(long projectId) {
        try {
            Connection connection = getConnection();
            Optional<List<File>> files = fileDao.find(connection, projectId);
            connection.close();
            return files;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public byte[] loadFile(long memberId, long fileId) {
        Optional<File> fileBox = fileDao.findOne(fileId);
        File file = fileBox.orElseThrow(IllegalArgumentException::new);

        StringBuilder builder = new StringBuilder();
        builder.append(file.getMemberId())
                .append("/")
                .append(file.getName());
        return download(builder.toString());
    }

    private byte[] download(String fileName) {
        try {
            S3Template template = new S3Template();
            return template.download(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private long upload(String path, String fileData) {
        try {
            S3Template template = new S3Template();
            return template.upload(path, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1L;
    }

}
