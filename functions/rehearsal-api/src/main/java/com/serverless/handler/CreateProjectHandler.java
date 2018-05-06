package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.dto.ProjectDto;
import com.serverless.service.ProjectService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.serverless.service.ProjectService.createProjectService;

public class CreateProjectHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(CreateProjectHandler.class);
    private ProjectService projectService;

    public CreateProjectHandler() {
        projectService = createProjectService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info(input);
        ProjectDto projectDto = getBody(input, ProjectDto.class);
        projectService.create(projectDto, getPathParams(input));
        return ApiGatewayResponse.builder()
                .setStatusCode(201)
                .build();
    }

}
