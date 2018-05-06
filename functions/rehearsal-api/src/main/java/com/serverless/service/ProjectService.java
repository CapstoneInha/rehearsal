package com.serverless.service;

import com.serverless.dao.FileDao;
import com.serverless.dao.HistoryDao;
import com.serverless.dao.ProjectDao;
import com.serverless.model.domain.File;
import com.serverless.model.domain.History;
import com.serverless.model.domain.Project;
import com.serverless.model.dto.ProjectDto;
import com.serverless.utility.enums.EventType;
import com.serverless.utility.enums.MediaType;
import com.serverless.utility.enums.State;
import com.serverless.utility.template.S3Template;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.serverless.utility.template.JdbcTemplate.getConnection;

public class ProjectService {
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

    public void create(ProjectDto projectDto, Map<String, String> pathParams) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            projectDto.setState(State.NOTYET);
            projectDto.setCreateAt(LocalDateTime.now());
            projectDto.setMemberId(Integer.parseInt(pathParams.get("memberId")));
            long projectId = projectDao.create(connection, new Project(projectDto));
            S3Template template = new S3Template();
            long fileSize = template.upload(projectDto.getMemberId() + "/" + projectDto.getFileName(), projectDto.getFile());

            Project project = new Project(projectDto);
            project.setId(projectId);

            File file = new File();
            file.setMemberId(Integer.parseInt(pathParams.get("memberId")));
            file.setName(projectDto.getFileName());
            file.setUpdatedAt(LocalDateTime.now());
            file.setCreatedAt(LocalDateTime.now());
            file.setSize(fileSize);
            file.setMediaType(MediaType.parse(projectDto.getMediaType()));

            long fileId = fileDao.create(connection, file);

            History history = new History();
            history.setFileId(fileId);
            history.setProjectId(projectId);
            history.setCreateAt(LocalDateTime.now());
            history.setEventType(EventType.CREATE_PROJECT);
            historyDao.create(connection, history);

            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public Optional<Project> findOne(long projectId) {
        Connection connection = null;
        try {
            connection = getConnection();
            Optional<Project> project = projectDao.findOne(connection, projectId);
            connection.close();
            return project;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<List<Project>> find(long memberId) {
        try {
            Connection connection = getConnection();
            Optional<List<Project>> projects = projectDao.find(connection, memberId);
            connection.close();
            return projects;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
