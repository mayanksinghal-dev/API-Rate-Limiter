package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimiter {
    public static void main(String[] args) {
        SpringApplication.run(RateLimiter.class,args);
    }
}