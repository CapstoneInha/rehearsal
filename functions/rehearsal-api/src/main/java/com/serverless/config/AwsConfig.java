package com.serverless.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.IOException;

public class AwsConfig {
    private AmazonS3 s3Client;

    public AwsConfig() throws IOException {
        PropertiesCredentials propertiesCredentials = new PropertiesCredentials(
                this
                        .getClass()
                        .getClassLoader()
                        .getResourceAsStream("app.properties")
        );

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(propertiesCredentials.getAWSAccessKeyId(), propertiesCredentials.getAWSSecretKey());
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }

}
