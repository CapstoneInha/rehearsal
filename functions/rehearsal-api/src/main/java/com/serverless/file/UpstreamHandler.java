package com.serverless.file;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dao.FileDao;
import com.serverless.model.domain.File;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.Response;
import com.serverless.utility.S3Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UpstreamHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);
        Map<String, Object> body = Collections.synchronizedMap((HashMap) input.get("message"));
        byte[] fileBytes = Base64.decodeBase64(body.get("fileStream").toString());
        String extension = body.get("extension").toString();

        Map<String, Object> pathParameters = Collections.synchronizedMap((HashMap) input.get("pathParameters"));
        String userId = pathParameters.get("userId").toString();
        long fileSize = process(userId, extension, fileBytes);

        Map<String, Object> fileObj = Collections.synchronizedMap((HashMap) body.get("file"));
        create(fileObj, fileSize);

        Response responseBody = new Response("Go Serverless v1.x! Your function executed successfully!", input);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }

    private long process(String userId, String extension, byte[] fileData) {
        try {
            S3Template template = new S3Template();
            return template.upload(userId, extension, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1L;
    }

    private void create(Map<String, Object> fileParam, long fileSize) {
        File file = new File();
        file.setName(fileParam.get("name").toString());
        file.setSize(fileSize);
        file.setCreatedAt(LocalDateTime.now());
        file.setUpdatedAt(LocalDateTime.now());
        file.setUserId(Long.parseLong(fileParam.get("userId").toString()));

        FileDao fileDao = new FileDao();
        try {
            fileDao.create(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
