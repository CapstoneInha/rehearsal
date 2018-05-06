package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.domain.Project;
import com.serverless.service.ProjectService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.serverless.service.ProjectService.createProjectService;

public class FindProjectsHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(FindProjectsHandler.class);
    private ProjectService projectService;

    public FindProjectsHandler() {
        projectService = createProjectService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map<String, String> params = getPathParams(input);
        List<Project> projects = projectService.find(Long.parseLong(params.get("memberId"))).orElseThrow(IllegalArgumentException::new);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(projects)
                .build();
    }
}
