package com.honeyosori.dogfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DogfileApplication {

    public static void main(String[] args) {

        SpringApplication.run(DogfileApplication.class, args);
    }

}
