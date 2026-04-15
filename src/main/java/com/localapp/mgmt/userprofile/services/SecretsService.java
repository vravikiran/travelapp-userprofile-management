package com.localapp.mgmt.userprofile.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Service
public class SecretsService {

    private final SecretsManagerClient client;

    public SecretsService() {
        this.client = SecretsManagerClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(ProfileCredentialsProvider.create("dev-role"))
                .build();
    }

    public String getPrivateKey() {

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId("prod/auth/rsa-private-key")
                .build();

        return client.getSecretValue(request).secretString();
    }
}