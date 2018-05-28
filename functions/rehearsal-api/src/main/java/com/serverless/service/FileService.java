package com.serverless.service;

import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.model.dto.FileDto;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.template.S3Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FileService {
    private static final Logger LOG = Logger.getLogger(FileService.class);
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
            // TODO: 2018. 5. 27. memberId = 2 임시값, 세션 적용필요
            long memberId = 2;
            long projectId = Long.parseLong(pathParams.get("projectId"));
            String mediaType = StringUtils.substringBetween(fileDto.getData(), "data:", ";base64,");
            String fileData = StringUtils.substringAfter(fileDto.getData(), ";base64,");
            System.out.println("mediaType: " + mediaType + ", data: " + fileData);
            long fileSize = upload(memberId + "/" + fileDto.getName(), fileData, mediaType);
            File file = new File(fileDto);
            file.setSize(fileSize);
            file.setMemberId(memberId);
            file.setCreatedAt(LocalDateTime.now());
            file.setUpdatedAt(LocalDateTime.now());
            file.setMediaType("audio/x-m4a");
            long fileId = fileDao.create(file);

            History history = new History();
            history.setFileId(fileId);
            history.setProjectId(projectId);
            history.setCreateAt(LocalDateTime.now());
            history.setEventType(EventType.CREATE_AUDIO);
            historyDao.create(history);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<File> find(long projectId) throws SQLSyntaxErrorException {
        try {
            return fileDao.find(projectId).orElseThrow(IllegalArgumentException::new);
        } catch (SQLException e) {
            LOG.error(e);
            throw new SQLSyntaxErrorException("잘못된 요청입니다.");
        }

    }

    private long upload(String path, String fileData, String type) {
        try {
            S3Template template = new S3Template();
            return template.upload(path, fileData, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1L;
    }

}
