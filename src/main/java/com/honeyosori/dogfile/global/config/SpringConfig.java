package com.honeyosori.dogfile.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SpringConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}