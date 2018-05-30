package com.serverless.utility.filter;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ApiGatewayRequest implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private ObjectMapper mapper;

    protected ObjectMapper getMapper() {
        return mapper;
    }

    protected ApiGatewayRequest() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
    }

    protected Map<String, String> getPathParams(Map<String, Object> input) {
        ConcurrentHashMap pathParams = new ConcurrentHashMap<>();
        Map pathParameters = (Map) input.get("pathParameters");
        pathParameters.forEach((key, value) -> {
            pathParams.put(key.toString(), value.toString());
        });

        return pathParams;
    }

    protected Map<String, String> getQueryString(Map<String, Object> input) {
        ConcurrentHashMap queryStrings = new ConcurrentHashMap<>();
        Map pathParameters = (Map) input.get("queryStringParameters");
        pathParameters.forEach((key, value) -> {
            queryStrings.put(key.toString(), value.toString());
        });

        return queryStrings;
    }

    protected <T> T getBody(Map<String, Object> input, Class<T> bodyClass) {
        try {
            return mapper.readValue(StringUtils.unwrap(input.get("body").toString(), "\""), bodyClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
