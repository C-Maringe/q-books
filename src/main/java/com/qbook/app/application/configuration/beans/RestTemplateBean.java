package com.qbook.app.application.configuration.beans;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateBean {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder, ResponseErrorHandler errorHandler) {
        return restTemplateBuilder
                .errorHandler(errorHandler)
                .build();
    }
}
