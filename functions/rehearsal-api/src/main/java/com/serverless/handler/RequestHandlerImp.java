package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.serverless.utility.ApiGatewayResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandlerImp <T> implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private Map<String, String> pathParams;
    private Map<String, Object> input;
    private T body;
    private ObjectMapper mapper;

    public RequestHandlerImp() {
        pathParams = new ConcurrentHashMap<>();
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        this.input  = input;
        return null;
    }

    protected Map<String, String> getPathParams() {
        Map pathParameters = (Map) input.get("pathParameters");
        pathParameters.forEach((key, value) -> {
            pathParams.put(key.toString(), value.toString());
        });
        return pathParams;
    }

    protected T getBody(Class<T> bodyClass) {
        try {
            return mapper.readValue(StringUtils.unwrap(input.get("body").toString(), "\""), bodyClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
