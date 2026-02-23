package com.example.career;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CareerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareerApplication.class, args);
    }
}