package com.qbook.app.application.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties("payment")
public class PaymentGatewayProperties {
    private String userId;
    private String password;
    private String entityId;
    private String baseUrl;
    private String resourcePath;
    private String authCode;
}
