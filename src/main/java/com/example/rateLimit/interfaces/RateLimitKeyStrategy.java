package com.example.rateLimit.interfaces;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RateLimitKeyStrategy {
    String resolveKeys(HttpServletRequest request);
}
