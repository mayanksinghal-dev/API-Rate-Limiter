package com.example.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.PublicKey;

@Configuration
public class CommonConfig {

    @Value("${jwt.public.key.path}")
    public String publicKeyPath;
}
