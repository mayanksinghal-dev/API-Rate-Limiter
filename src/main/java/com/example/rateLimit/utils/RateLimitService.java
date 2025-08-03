package com.example.rateLimit.utils;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.rateLimit.config.RateLimitConfig;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Autowired
    private StrategyResolver strategyResolver;

    public boolean isApprove(HttpServletRequest request) {
        List<RateLimitKeyStrategy> strategies = strategyResolver.resolveStrategy(request);
        Set<String> allKeys = strategies.stream().map(strategy-> strategy.resolveKeys(request)).collect(Collectors.toSet());
        for(String key: allKeys){
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
}
