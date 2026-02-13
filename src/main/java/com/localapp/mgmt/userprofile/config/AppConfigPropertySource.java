package com.localapp.mgmt.userprofile.config;


import com.localapp.mgmt.userprofile.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.util.Objects;
import java.util.Properties;

/**
 * Defines AppConfigPropertySource
 * application configuration are retrieved AWS AppConfig
 * Assumes aws role using role ARN
 * aws credentials are fetched from aws role
 * using aws role retrieves the application properties from application configuration defined in AWS AppConfig
 * application properties are retrieved based on application identifier, environment and configuration profile name
 */
@Component
public class AppConfigPropertySource {
    Logger log = LoggerFactory.getLogger(AppConfigPropertySource.class);

    private Properties properties;

    public Object[] getPropertyNames() {
        return this.properties.keySet().toArray();
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void init() {
        AssumeRoleRequest roleRequest = AssumeRoleRequest.builder().roleArn(Constants.roleARN).roleSessionName(Constants.roleSessionName).build();
        AssumeRoleResponse roleResponse;
        try (StsClient stsClient = StsClient.builder().build()) {
            roleResponse = stsClient.assumeRole(roleRequest);
        }
        Credentials awsBasicCredentials = roleResponse.credentials();
        GetLatestConfigurationResponse latestConfigurationResponse;
        try (AppConfigDataClient client = AppConfigDataClient.builder().credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey(), awsBasicCredentials.sessionToken()))).build()) {
            StartConfigurationSessionRequest startConfigurationSessionRequest = StartConfigurationSessionRequest.builder().applicationIdentifier(Constants.appIdentifier).environmentIdentifier(Constants.envIdentifier).configurationProfileIdentifier(Constants.profileIdentifier).build();
            String sessionToken = client.startConfigurationSession(startConfigurationSessionRequest).initialConfigurationToken();
            GetLatestConfigurationRequest latestConfigurationRequest = GetLatestConfigurationRequest.builder().configurationToken(sessionToken).build();
            latestConfigurationResponse = client.getLatestConfiguration(latestConfigurationRequest);
        }
        byte[] buffer = latestConfigurationResponse.configuration().asByteArray();
        processYamlContent(buffer);
    }

    private void processYamlContent(byte[] byteBuffer) {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ByteArrayResource(byteBuffer));
        this.properties = bean.getObject();
    }
}