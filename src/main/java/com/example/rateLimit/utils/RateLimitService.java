package com.example.rateLimit.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.rateLimit.config.RateLimitConfig;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    public boolean isApprove(HttpServletRequest request) {
        String ip = Optional.ofNullable(request.getHeader("ip"))
                .orElse(request.getRemoteAddr());
        String userId = Optional.ofNullable(request.getHeader("userId"))
                .orElse("anonymous");
        String uri = request.getRequestURI();
        String ipKey = "rl:ip:" + ip + ":" + uri;
        String userKey = "rl:user:" + userId + ":" + uri;
        Long userCount = redisTemplate.opsForValue().increment(userKey);
        if (userCount == 1) {
            redisTemplate.expire(userKey, Duration.ofSeconds(rateLimitConfig.blockSeconds));
        }
        Long ipCount = redisTemplate.opsForValue().increment(ipKey);
        if (ipCount == 1) {
            redisTemplate.expire(ipKey, Duration.ofSeconds(rateLimitConfig.blockSeconds));
        }
        if (ipCount >= rateLimitConfig.rateLimit || userCount >= rateLimitConfig.rateLimit) {
            log.warn("Rate limit exceeded: IP={}, User={}, URI={}",ip,userId,uri);
            return false;
        }
        return true;
    }
}
