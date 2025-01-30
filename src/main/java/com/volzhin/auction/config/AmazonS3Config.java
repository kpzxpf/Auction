package com.volzhin.auction.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AmazonS3Config {
    @Value("${spring.services.s3.accessKey}")
    private String accessKey;
    @Value("${spring.services.s3.secretKey}")
    private String secretKey;
    @Value("${spring.services.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 s3Client(){
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(new CreateBucketRequest(bucketName));
        }
        return s3Client;
    }
}