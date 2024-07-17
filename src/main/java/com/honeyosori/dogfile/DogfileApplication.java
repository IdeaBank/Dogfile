package com.honeyosori.dogfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class DogfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(DogfileApplication.class, args);
    }

}
