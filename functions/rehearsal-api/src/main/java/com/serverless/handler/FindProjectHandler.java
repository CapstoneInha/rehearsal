package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.domain.Project;
import com.serverless.model.dto.ProjectResponse;
import com.serverless.service.ProjectService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.sql.SQLSyntaxErrorException;
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
        Map<String, String> params = getPathParams(input);
        ApiGatewayResponse response;
        try {
            ProjectResponse projectResponse = projectService.findOne(Long.parseLong(params.get("projectId")));
            response = ApiGatewayResponse.builder()
                    .setStatusCode(HttpStatus.SC_OK)
                    .setObjectBody(projectResponse)
                    .build();
        } catch (SQLSyntaxErrorException e) {
            response = ApiGatewayResponse.builder()
                    .setStatusCode(HttpStatus.SC_NOT_FOUND)
                    .setObjectBody(e.getMessage())
                    .build();
        }
        return response;
    }

}
