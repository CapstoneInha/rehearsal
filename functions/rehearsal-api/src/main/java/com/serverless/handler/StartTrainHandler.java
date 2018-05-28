package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.dto.ProjectRequest;
import com.serverless.service.ProjectService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.sql.SQLSyntaxErrorException;
import java.util.Map;

import static com.serverless.service.ProjectService.createProjectService;

/**
 * Created by koseungbin on 2018. 5. 29.
 */

public class StartTrainHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(StartTrainHandler.class);
    private ProjectService projectService;

    public StartTrainHandler() {
        projectService = createProjectService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info(input);
        ProjectRequest projectDto = getBody(input, ProjectRequest.class);
        try {
            projectService.update(projectDto);
        } catch (SQLSyntaxErrorException e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .setObjectBody(e.getMessage())
                    .build();
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(HttpStatus.SC_OK)
                .build();
    }
}
