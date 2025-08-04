package com.example.rateLimit.algorithm;

import com.example.common.constants.Constants;
import com.example.rateLimit.config.RateLimitConfig;
import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component(Constants.Tier.PREMIUM_TIER)
public class TokenBucket implements RateLimitAlgorithm {
    private final double refillRate;
    private final int capacity;
    private double tokens;
    private long lastRefillTimestamp;

    @Autowired
    private FixedWindow fixedWindow;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBucket(RateLimitConfig rateLimitConfig) {
        this.refillRate = rateLimitConfig.tokenRefillRate;
        this.capacity = rateLimitConfig.tokenCapacity;
        this.tokens = rateLimitConfig.tokenCapacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean isApproved(Set<String> keys) {
        refillRate();
        if (tokens >= 1) {
            boolean result = fixedWindow.isApproved(keys);
            if (result) {
                tokens--;
            }
            return result;
        }
        return false;
    }

    private void refillRate() {
        long now = System.nanoTime();
        double secondsSinceLast = (now - lastRefillTimestamp) / 1_000_000_000.0;
        double newTokens = secondsSinceLast * refillRate;
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTimestamp = now;
    }
}
