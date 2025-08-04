package com.example.rateLimit.algorithm;

import com.example.common.constants.Constants;
import com.example.rateLimit.config.RateLimitConfig;
import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Component(Constants.Tier.FREE_TIER)
public class FixedWindow implements RateLimitAlgorithm {
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Override
    public boolean isApproved(Set<String> keys){
        try{
            for(String key: keys){
                Long count = redisTemplate.opsForValue().increment(key);
                if (count == 1) {
                    redisTemplate.expire(key, Duration.ofSeconds(rateLimitConfig.blockSeconds));
                }
                if (count >= rateLimitConfig.rateLimit) {
                    log.warn("Rate limit exceeded: userDetails={}",key);
                    return false;
                }
            }
            return true;
        }
        catch(Exception exp){
            log.warn("[FixedWindow:isApproved] Fixed window validation failed for keys={}",keys);
            return false;
        }
    }
}
