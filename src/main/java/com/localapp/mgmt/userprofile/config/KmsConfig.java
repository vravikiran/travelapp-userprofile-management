package com.localapp.mgmt.userprofile.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class KmsConfig {
    @Value("${aws.kms.key}")
    private String key;

    public KmsConfig() {
        super();
    }

    public KmsConfig(String key) {
        super();
        this.key = key;
    }
}
