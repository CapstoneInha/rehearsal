package com.serverless.file;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.utility.ApiGatewayResponse;
import com.serverless.utility.Response;
import com.serverless.utility.S3Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DownstreamHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(DownstreamHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);
        Map<String, Object> body = Collections.synchronizedMap((HashMap) input.get("message"));
        String fileName = body.get("file").toString();
        byte[] data = process(fileName);
        // TODO: 2018. 4. 19. add content type 
        Response responseBody = new Response(Base64.encodeBase64String(data), input);
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
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
