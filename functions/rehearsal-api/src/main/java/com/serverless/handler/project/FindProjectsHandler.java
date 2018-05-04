package com.serverless.handler.project;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.dao.ProjectDao;
import com.serverless.handler.RequestHandlerImp;
import com.serverless.model.domain.Project;
import com.serverless.utility.ApiGatewayResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FindProjectsHandler extends RequestHandlerImp<Project> {


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        super.handleRequest(input, context);
        Map<String, String> params = getPathParams();
        ProjectDao projectDao = new ProjectDao();
        Optional<List<Project>> projectsBox =  projectDao.findProjects(Integer.parseInt(params.get("userId")));
        List<Project> projects = projectsBox.orElseThrow(IllegalArgumentException::new);

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(projects)
                .build();
    }
}
