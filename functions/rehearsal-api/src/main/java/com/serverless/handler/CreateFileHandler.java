package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.model.dto.FileDto;
import com.serverless.service.FileService;
import com.serverless.utility.filter.ApiGatewayRequest;
import com.serverless.utility.filter.ApiGatewayResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
        LOG.info(input.toString());
        List<FileDto> fileDtos = getBodyToList(input);
        fileService.create(fileDtos, getPathParams(input));
        return ApiGatewayResponse.builder()
                .setStatusCode(201)
                .build();
    }

    private List<FileDto> getBodyToList(Map<String, Object> input) {
        try {
            return Arrays.asList(getMapper().readValue(StringUtils.unwrap(input.get("body").toString(), "\""), FileDto[].class));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

}
