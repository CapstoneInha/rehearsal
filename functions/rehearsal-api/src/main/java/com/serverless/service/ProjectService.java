package com.serverless.service;

import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.dao.ProjectDao;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.model.domain.Project;
import com.serverless.model.dto.ProjectRequest;
import com.serverless.model.dto.ProjectResponse;
import com.serverless.utility.Constants;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.template.S3Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectService {
    private static final Logger LOG = Logger.getLogger(ProjectService.class);
    private static ProjectService projectService;
    private ProjectDao projectDao;
    private HistoryDao historyDao;
    private FileDao fileDao;

    private ProjectService() {
        projectDao = ProjectDao.createProjectDao();
        historyDao = HistoryDao.createHistoryDao();
        fileDao = FileDao.createFileDao();
    }

    public static ProjectService createProjectService() {
        if (projectService == null) {
            projectService = new ProjectService();
        }

        return projectService;
    }

    public void create(ProjectRequest projectDto) {
        try {
            // TODO: 2018. 5. 27. memberId = 2 임시값, 세션 적용필요
            long memberId = 2;
            projectDto.setMemberId(memberId);
            projectDto.setState("NOTYET");
            projectDto.setCreateAt(LocalDateTime.now());
            long projectId = projectDao.create(new Project(projectDto));
            String mediaType = StringUtils.substringBetween(projectDto.getFile(), "data:", ";base64,");
            String fileData = StringUtils.substringAfter(projectDto.getFile(), ";base64,");
            S3Template template = new S3Template();
            long fileSize = template.upload(projectDto.getMemberId() + "/" + projectDto.getFileName(), fileData, mediaType);

            Project project = new Project(projectDto);
            project.setId(projectId);

            File file = new File();
            file.setMemberId(memberId);
            file.setName(projectDto.getFileName());
            file.setUpdatedAt(LocalDateTime.now());
            file.setCreatedAt(LocalDateTime.now());
            file.setSize(fileSize);
            file.setMediaType("application/pdf");

            long fileId = fileDao.create(file);

            History history = new History();
            history.setFileId(fileId);
            history.setProjectId(projectId);
            history.setCreateAt(LocalDateTime.now());
            history.setEventType(EventType.CREATE_PROJECT);
            historyDao.create(history);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public ProjectResponse findOne(long projectId) throws SQLSyntaxErrorException {
        try {
            Project project = projectDao.findOne(projectId).orElseThrow(IllegalArgumentException::new);
            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(project.getId());
            projectResponse.setTitle(project.getTitle());
            projectResponse.setPlot(project.getPlot());
            projectResponse.setState(project.getState());
            projectResponse.setCreateAt(project.getCreateAt());
            projectResponse.setImagePath(Constants.S3_URL + project.getMemberId() + "/" + project.getFileName());
            return projectResponse;
        } catch (SQLException e) {
            LOG.error(e);
            throw new SQLSyntaxErrorException("잘못된 요청입니다.");
        }

    }

    public List<ProjectResponse> find(long memberId) throws SQLSyntaxErrorException {
        try {
            List<Project> projects = projectDao.find(memberId).orElseThrow(IllegalArgumentException::new);
            return projects.stream().map(project -> {
                ProjectResponse projectResponse = new ProjectResponse();
                projectResponse.setId(project.getId());
                projectResponse.setTitle(project.getTitle());
                projectResponse.setPlot(project.getPlot());
                projectResponse.setState(project.getState());
                projectResponse.setCreateAt(project.getCreateAt());
                projectResponse.setImagePath(Constants.S3_URL + project.getMemberId() + "/" + project.getFileName());
                return projectResponse;
            }).collect(Collectors.toList());

        } catch (SQLException e) {
            LOG.error(e);
            throw new SQLSyntaxErrorException("잘못된 요청입니다.");
        }
    }

}
