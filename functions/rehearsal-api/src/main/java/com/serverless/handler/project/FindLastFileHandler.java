package com.serverless.handler.project;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.serverless.dao.FileDao;
import com.serverless.model.domain.File;
import com.serverless.utility.ApiGatewayResponse;
import org.apache.log4j.Logger;

import java.util.Map;

public class FindLastFileHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(FindFileHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map pathParameters = (Map) input.get("pathParameters");
        File file = process(Integer.parseInt(pathParameters.get("projectId").toString()));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        String message = null;
        try {
            message = mapper.writeValueAsString(file);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(message)
                .build();
    }

    private File process(int projectId){
        FileDao fileDao = new FileDao();
        return fileDao.findLastOne(projectId).get();
    }

}
