package com.miruni.backend.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties (
    String bucket,

    String region,

    String accessKey,

    String secretKey
){
    public String getBaseUrl(){
        return String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
    }
}
