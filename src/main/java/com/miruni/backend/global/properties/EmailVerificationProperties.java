package com.miruni.backend.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "miruni.email.verification")
public record EmailVerificationProperties (
        String prefix,
        long expireMinutes,
        String fromAddress,
        String fromName,
        String subject
){

}       