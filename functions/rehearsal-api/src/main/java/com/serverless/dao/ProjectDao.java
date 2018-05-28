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


//    public Optional<Project> findLastProjectByMemberId(Connection connection, int memberId) throws SQLException {
//        Statement statement = null;
//        ResultSet resultSet = null;
//        try {
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(StringUtils.replace(ProjectSQL.FIND_LAST_PROJECT_BY_MEMBER_ID, "#id", String.valueOf(memberId)));
//            Project project = null;
//            if (resultSet.next()) {
//                project = new Project();
//                project.setId(resultSet.getLong("id"));
//                project.setTitle(resultSet.getString("title"));
//                project.setPlot(resultSet.getString("plot"));
//                project.setState(State.valueOf(resultSet.getString("state")));
//                project.setCreateAt(resultSet.getTimestamp("create_at").toLocalDateTime());
//                project.setMemberId(resultSet.getLong("member_id"));
//                project.setFileId(resultSet.getLong("file_id"));
//            }
//
//            resultSet.close();
//            statement.close();
//            return Optional.of(project);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return Optional.empty();
//    }

}


