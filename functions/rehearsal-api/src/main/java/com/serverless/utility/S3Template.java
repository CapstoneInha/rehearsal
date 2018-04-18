package com.serverless.utility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.serverless.config.AwsConfig;

import java.io.*;

public class S3Template {
    private AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "www.rehearsal-api.com";


    public S3Template() throws IOException {
        amazonS3 = new AwsConfig().getS3Client();
    }

    public long upload(String path, String extension, byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/" + extension);
        PutObjectResult result = amazonS3.putObject(BUCKET_NAME, "/audio/" + path + "." + extension, inputStream, objectMetadata);

        return result.getMetadata().getContentLength();
    }

    public byte[] download(String path) throws IOException {
        S3Object object = amazonS3.getObject(BUCKET_NAME, path);
        return IOUtils.toByteArray(object.getObjectContent());
    }
}
