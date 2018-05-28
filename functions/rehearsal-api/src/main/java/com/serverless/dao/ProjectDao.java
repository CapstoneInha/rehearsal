package com.serverless.dao;

import com.serverless.model.domain.Project;
import com.serverless.sql.ProjectSQL;
import com.serverless.utility.template.JdbcTemplate;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectDao {
    private static ProjectDao projectDao;

    private ProjectDao() {
    }

    public static ProjectDao createProjectDao() {
        if (projectDao == null) {
            projectDao = new ProjectDao();
        }

        return projectDao;
    }

    public long create(Project project) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        long insertId = jdbcTemplate.create(ProjectSQL.CREATE_PROJECTS, project);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return insertId;
    }

    public void update(Project project) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.update(ProjectSQL.UPDATE_PROJECTS_BY_ID, project);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
    }

    public Optional<List<Project>> find(long memberId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Map<String, Object> params = new ConcurrentHashMap<>();
        params.put("memberId", memberId);
        LinkedList<Project> projects = jdbcTemplate.find(ProjectSQL.FIND_PROJECTS_BY_USER_ID, params, Project.class);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return Optional.ofNullable(projects);
    }

    public Optional<Project> findOne(long projectId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Map<String, Object> params = new ConcurrentHashMap<>();
        params.put("projectId", projectId);
        LinkedList<Project> projects = jdbcTemplate.find(ProjectSQL.FIND_PROJECT_BY_ID, params, Project.class);
        Project project = projects.isEmpty() ? null : projects.getFirst();
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return Optional.ofNullable(project);
    }

}


