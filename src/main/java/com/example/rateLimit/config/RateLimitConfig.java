package com.example.rateLimit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${rateLimit:10}")
    public int rateLimit;

    @Value("${blockSeconds:60}")
    public int blockSeconds;

    @Value("${tokenCapacity:60}")
    public int tokenCapacity;

    @Value("${tokenRefillRate:5}")
    public int tokenRefillRate;
}

