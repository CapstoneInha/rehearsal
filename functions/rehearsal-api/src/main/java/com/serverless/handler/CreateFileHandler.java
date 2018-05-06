package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.dto.FileDto;
import com.serverless.service.FileService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.serverless.service.FileService.createFileService;

public class CreateFileHandler extends ApiGatewayRequest {
    private static final Logger LOG = Logger.getLogger(CreateFileHandler.class);
    private FileService fileService;

    public CreateFileHandler() {
        fileService = createFileService();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        FileDto fileDto = getBody(input, FileDto.class);
        fileService.create(fileDto, getPathParams(input));
        return ApiGatewayResponse.builder()
                .setStatusCode(201)
                .build();
    }

}
