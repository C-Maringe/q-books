package com.qbook;

import com.qbook.app.application.filters.RestFilter;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@EnableScheduling
@SpringBootApplication
@CrossOrigin
public class Application {

    @Autowired
    private AuthTokenServices authTokenServices;

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        return new FilterRegistrationBean(new RestFilter(authTokenServices));
    }
}
