package com.serverless.file;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dao.FileDao;
import com.serverless.model.domain.File;
import com.serverless.model.dto.FileDto;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.Response;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoadFileHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);
        List<File> files = process();
        Response responseBody = new Response(files.toString(), input);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }

    private List<File> process(){
        FileDao fileDao = new FileDao();
        return fileDao.find(1).get();
    }

}
