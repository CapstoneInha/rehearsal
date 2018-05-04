package com.serverless.utility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.serverless.config.AwsConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class S3Template {
    private AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "www.rehearsal-api.com";


    public S3Template() throws IOException {
        amazonS3 = new AwsConfig().getS3Client();
    }

    public long upload(String path, String data) {
        PutObjectResult result = amazonS3.putObject(BUCKET_NAME, path, data);

        return result.getMetadata().getContentLength();
    }

    public byte[] download(String path) throws IOException {
        S3Object object = amazonS3.getObject(BUCKET_NAME, path);
        S3ObjectInputStream in = object.getObjectContent();
        return IOUtils.toByteArray(in);
    }
}
