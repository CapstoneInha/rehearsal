package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.service.FileService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static com.serverless.service.FileService.createFileService;

public class DownstreamHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);
    private FileService fileService;

    public DownstreamHandler() {
        fileService = createFileService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map<String, String> pathParameters = getPathParams(input);
        long fileId = Long.parseLong(pathParameters.get("fileId"));
        long memberId = Long.parseLong(pathParameters.get("memberId"));
        byte[] bytes = fileService.loadFile(memberId, fileId);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new String(bytes, StandardCharsets.UTF_8))
                .build();
    }

}
