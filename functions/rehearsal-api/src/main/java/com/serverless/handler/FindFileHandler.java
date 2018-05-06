package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.domain.File;
import com.serverless.service.FileService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.serverless.service.FileService.createFileService;

public class FindFileHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(FindFileHandler.class);
    private FileService fileService;

    public FindFileHandler() {
        fileService = createFileService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map pathParameters = getPathParams(input);
        List<File> files = fileService.find(Long.parseLong(pathParameters.get("projectId").toString())).orElseThrow(IllegalArgumentException::new);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(files)
                .build();
    }
}
