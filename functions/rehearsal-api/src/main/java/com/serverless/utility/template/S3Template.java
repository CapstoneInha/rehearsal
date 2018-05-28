package com.serverless.utility.template;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.serverless.config.AwsConfig;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3Template {
    private AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "www.rehearsal-api.com";

    public S3Template() throws IOException {
        amazonS3 = new AwsConfig().getS3Client();
    }

    public long upload(String path, String data, String type) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader("x-amz-acl", "public-read");
        metadata.setContentType(type);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(data));
        PutObjectResult result = amazonS3.putObject(BUCKET_NAME, path, inputStream, metadata);
        return result.getMetadata().getContentLength();
    }

    public byte[] download(String path) throws IOException {
        S3Object object = amazonS3.getObject(BUCKET_NAME, path);
        S3ObjectInputStream in = object.getObjectContent();
        return IOUtils.toByteArray(in);
    }

}
