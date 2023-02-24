package com.qbook.app.application.configuration.beans;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GsonBean {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
