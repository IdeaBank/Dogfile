package com.honeyosori.dogfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.Map;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DogfileApplication {

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env.get(envName));
        }

        SpringApplication.run(DogfileApplication.class, args);
    }

}
