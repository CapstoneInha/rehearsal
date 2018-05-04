package com.serverless.handler.project;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.dao.ProjectDao;
import com.serverless.handler.RequestHandlerImp;
import com.serverless.model.domain.Project;
import com.serverless.utility.ApiGatewayResponse;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class FindProjectHandler extends RequestHandlerImp<Project> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        super.handleRequest(input, context);
        Map<String, String> params = getPathParams();
        ProjectDao projectDao = new ProjectDao();
        Optional<Project> projectsBox = null;
        try {
            projectsBox = projectDao.findProject(Integer.parseInt(params.get("projectId")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Project project = projectsBox.orElseThrow(IllegalArgumentException::new);

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(project)
                .build();
    }
}
