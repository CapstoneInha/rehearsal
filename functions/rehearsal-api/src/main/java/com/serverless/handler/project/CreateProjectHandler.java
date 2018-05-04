package com.serverless.handler.project;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.dao.ProjectDao;
import com.serverless.handler.RequestHandlerImp;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.model.domain.Project;
import com.serverless.model.dto.ProjectDto;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.S3Template;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.enums.MediaType;
import com.serverless.utility.enums.State;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

public class CreateProjectHandler extends RequestHandlerImp<ProjectDto> {

    private static final Logger LOG = Logger.getLogger(CreateProjectHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        super.handleRequest(input, context);

        ProjectDto projectDto = super.getBody(ProjectDto.class);
        projectDto.setState(State.NOTYET);
        projectDto.setCreateAt(LocalDateTime.now());
        projectDto.setMemberId(Integer.parseInt(getPathParams().get("userId")));
        ProjectDao projectDao = new ProjectDao();
        String format;
        switch (projectDto.getMediaType()) {
            case "application/pdf":
                format = "pdf";
                break;
            case "audio/x-m4a":
                format = "audio";
                break;
            default:
                format = "";
        }

        long fileSize = 0;
        long projectId = 0;
        try {
            projectId = projectDao.create(new Project(projectDto));
            S3Template template = new S3Template();
            fileSize = template.upload(format + "/" + projectDto.getMemberId() + "/" + projectDto.getFileName(), projectDto.getFile());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        File file = new File();
        file.setMemberId(Integer.parseInt(getPathParams().get("userId")));
        file.setName(projectDto.getFileName());
        file.setUpdatedAt(LocalDateTime.now());
        file.setCreatedAt(LocalDateTime.now());
        file.setSize(fileSize);
        file.setMediaType(MediaType.parse(projectDto.getMediaType()));
        FileDao fileDao = new FileDao();
        long fileId = 0;
        try {
            fileId = fileDao.create(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LOG.info("fileId: " + fileId + ", projectId: " + projectId);

        History history = new History();
        history.setFileId(fileId);
        history.setProjectId(projectId);
        history.setCreateAt(LocalDateTime.now());
        history.setEventType(EventType.CREATE_PROJECT);
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

}
