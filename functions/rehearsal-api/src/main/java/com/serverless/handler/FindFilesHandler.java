package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.domain.File;
import com.serverless.service.FileService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Map;

import static com.serverless.service.FileService.createFileService;

public class FindFilesHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(FindFilesHandler.class);
    private FileService fileService;

    public FindFilesHandler() {
        fileService = createFileService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map pathParameters = getPathParams(input);
        ApiGatewayResponse response;
        try {
            List<File> files = fileService.find(Long.parseLong(pathParameters.get("projectId").toString()));
            response = ApiGatewayResponse.builder()
                    .setStatusCode(HttpStatus.SC_OK)
                    .setObjectBody(files)
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
