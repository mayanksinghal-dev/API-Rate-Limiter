package com.example.rateLimit.algorithm;

import com.example.common.constants.Constants;
import com.example.rateLimit.config.RateLimitConfig;
import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component(Constants.Tier.PREMIUM_TIER)
public class TokenBucket implements RateLimitAlgorithm {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisScript<Long> tokenBucketScript;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Autowired
    private FixedWindow fixedWindow;

    /**
     * Validates premium users against userId/IP with no of requests with higher no of requests allowed and better precised algorithm.
     * @param keys Dynamic keys used in redis to verify user rates
     * @return boolean reflecting if user/IP is abuser or not
     */
    @Override
    public boolean isApproved(Set<String> keys) {
        if (!fixedWindow.isApproved(keys))
            return false;
        String tokensKey = "bucket:tokens";
        String timestampKey = "bucket:timestamp";
        long now = System.currentTimeMillis() / 1000; // seconds
        List<String> redisKeys = Arrays.asList(tokensKey, timestampKey);
        List<String> args = Arrays.asList(
                String.valueOf(rateLimitConfig.tokenCapacity),
                String.valueOf(rateLimitConfig.tokenRefillRate),
                String.valueOf(now)
        );

        Long result = redisTemplate.execute(tokenBucketScript, redisKeys, args.toArray());
        if (result == null || result == 0) {
            return false; // rate limit exceeded

        }
        return true;
    }
}
