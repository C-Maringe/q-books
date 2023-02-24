package com.qbook.app.application.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("firebase")
@Data
public class FirebaseProperties {
    private String serverKey;
    private String url;
    private String application;
    private String batchImportUrl;
}
