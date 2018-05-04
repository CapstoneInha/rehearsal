package com.serverless.handler.file;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dao.FileDao;
import com.serverless.model.domain.File;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.S3Template;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DownstreamHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        Map pathParameters = (Map) input.get("pathParameters");
        String fileId = pathParameters.get("fileId").toString();
        String userId = pathParameters.get("userId").toString();
        FileDao fileDao = new FileDao();
        Optional<File> fileBox = fileDao.findOne(Integer.parseInt(fileId));
        File file = fileBox.orElseThrow(IllegalArgumentException::new);
        String format;
        switch (file.getMediaType()) {
            case application_pdf:
                format = "pdf";
                break;
            case audio_x_m4a:
                format = "audio";
                break;
            default:
                format = "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(format)
                .append("/")
                .append(file.getMemberId())
                .append("/")
                .append(file.getName());
        byte[] data = process(builder.toString());
        Map<String, String> header = new ConcurrentHashMap<>();
        header.put("Content-Type", "application/json");

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setHeaders(header)
                .setObjectBody(new String(data, StandardCharsets.UTF_8))
                .build();
    }

    private byte[] process(String fileName) {
        try {
            S3Template template = new S3Template();
            return template.download(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
