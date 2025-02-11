package com.honeyosori.dogfile.global.config;

import feign.okhttp.OkHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.honeyosori.dogfile.domain"})
public class FeignOKHttpConfig {
    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}
