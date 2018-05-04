package com.serverless.handler.file;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.S3Template;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.enums.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

public class UpstreamHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Map body = null;
        Map pathParameters = null;
        try {
            body = mapper.readValue(StringUtils.unwrap(input.get("body").toString(), "\""), Map.class);
            pathParameters = (Map) input.get("pathParameters");
        } catch (IOException e) {
            e.printStackTrace();
        }


        String userId = pathParameters.get("userId").toString();
        String format;
        switch (body.get("type").toString()) {
            case "application/pdf":
                format = "pdf";
                break;
            case "audio/x-m4a":
                format = "audio";
                break;
            default:
                format = "";
        }
        long fileSize = process(format + "/" + body.get("memberId") + "/" + body.get("name"), body.get("data").toString());

        File file = new File();
        file.setMemberId(Integer.parseInt(userId));
        file.setName(body.get("name").toString());
        file.setUpdatedAt(LocalDateTime.now());
        file.setCreatedAt(LocalDateTime.now());
        file.setSize(fileSize);
        file.setMediaType(MediaType.parse(body.get("type").toString()));
        FileDao fileDao = new FileDao();
        long fileId = 0;
        try {
            fileId = fileDao.create(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LOG.info("fileId: " + fileId + ", projectId: " + body.get("projectId"));

        History history = new History();
        history.setFileId(fileId);
        history.setProjectId(Long.parseLong(body.get("projectId").toString()));
        history.setCreateAt(LocalDateTime.now());
        history.setEventType(EventType.CREATE_AUDIO);
        HistoryDao historyDao = new HistoryDao();
        try {
            historyDao.create(history);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(201)
                .build();
    }

    private long process(String path, String fileData) {
        try {
            S3Template template = new S3Template();
            return template.upload(path, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1L;
    }
}
