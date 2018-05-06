package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.domain.Project;
import com.serverless.service.ProjectService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.serverless.service.ProjectService.createProjectService;

public class FindProjectHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(FindProjectHandler.class);
    private ProjectService projectService;

    public FindProjectHandler() {
        projectService = createProjectService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("receive: " + input);
        Map<String, String> params = getPathParams(input);
        Project project = projectService.findOne(Long.parseLong(params.get("projectId"))).orElseThrow(IllegalArgumentException::new);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(project)
                .build();
    }

}
