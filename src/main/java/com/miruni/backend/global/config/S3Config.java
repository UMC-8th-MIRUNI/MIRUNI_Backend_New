package com.miruni.backend.global.config;

import com.miruni.backend.global.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Properties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
                .httpClient(UrlConnectionHttpClient.create())  
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(s3Properties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
                .build();
    }

    private AwsBasicCredentials createCredentials() {
        return AwsBasicCredentials.create(
                s3Properties.accessKey(),
                s3Properties.secretKey());
    }
}
