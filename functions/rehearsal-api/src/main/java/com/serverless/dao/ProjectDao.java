package com.serverless.dao;

import com.serverless.model.domain.Project;
import com.serverless.sql.ProjectSQL;
import com.serverless.utility.enums.State;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    public long create(Connection connection, Project project) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(ProjectSQL.CREATE_PROJECTS, Statement.RETURN_GENERATED_KEYS);
        ResultSet resultSet = null;
        try {
            preparedStatement.setString(1, project.getTitle());
            preparedStatement.setString(2, project.getPlot());
            preparedStatement.setString(3, project.getState().name());
            preparedStatement.setDate(4, new Date(project.getCreateAt().toEpochSecond(ZoneOffset.UTC)));
            preparedStatement.setLong(5, project.getMemberId());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            preparedStatement.close();
        }

        return -1;
    }

    public Optional<List<Project>> find(Connection connection, long memberId) {
        try {
            Statement statement = connection.createStatement();
            List<Project> projects = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery(ProjectSQL.FIND_PROJECTS_BY_USER_ID + memberId);
            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getLong("id"));
                project.setTitle(resultSet.getString("title"));
                project.setPlot(resultSet.getString("plot"));
                project.setState(State.valueOf(resultSet.getString("state")));
                project.setCreateAt(resultSet.getTimestamp("create_at").toLocalDateTime());
                project.setMemberId(resultSet.getLong("member_id"));
                project.setFileId(resultSet.getLong("file_id"));
                projects.add(project);
            }

            resultSet.close();
            statement.close();
            return Optional.of(projects);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Project> findOne(Connection connection, long projectId) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ProjectSQL.FIND_PROJECT_BY_ID + projectId);
            Project project = null;
            if (resultSet.next()) {
                project = new Project();
                project.setId(resultSet.getLong("id"));
                project.setTitle(resultSet.getString("title"));
                project.setPlot(resultSet.getString("plot"));
                project.setState(State.valueOf(resultSet.getString("state")));
                project.setCreateAt(resultSet.getTimestamp("create_at").toLocalDateTime());
                project.setMemberId(resultSet.getLong("member_id"));
                project.setFileId(resultSet.getLong("file_id"));
            }

            resultSet.close();
            statement.close();
            return Optional.of(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public Optional<Project> findLastProjectByMemberId(Connection connection, int memberId) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(StringUtils.replace(ProjectSQL.FIND_LAST_PROJECT_BY_MEMBER_ID, "#id", String.valueOf(memberId)));
            Project project = null;
            if (resultSet.next()) {
                project = new Project();
                project.setId(resultSet.getLong("id"));
                project.setTitle(resultSet.getString("title"));
                project.setPlot(resultSet.getString("plot"));
                project.setState(State.valueOf(resultSet.getString("state")));
                project.setCreateAt(resultSet.getTimestamp("create_at").toLocalDateTime());
                project.setMemberId(resultSet.getLong("member_id"));
                project.setFileId(resultSet.getLong("file_id"));
            }

            resultSet.close();
            statement.close();
            return Optional.of(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}


