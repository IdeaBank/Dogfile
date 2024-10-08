package com.honeyosori.dogfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DogfileApplication {

    public static void main(String[] args) {

        System.out.println("DOGGATE 시작");
        SpringApplication.run(DogfileApplication.class, args);
    }

}
