package com.example.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${rateLimit:10}")
    public int rateLimit;

    @Value("${blockSeconds:60}")
    public int blockSeconds;
}
