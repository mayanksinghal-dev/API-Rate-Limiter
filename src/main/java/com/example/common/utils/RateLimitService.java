package com.example.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RateLimitService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final int RATE_LIMIT = 10;
    private static final long BLOCK_SECONDS = 60;

    public boolean isApprove(HttpServletRequest request){
        Map<String,String> userDetails = this.extractUserDetails(request);
        String ip = Optional.ofNullable(request.getHeader("ip"))
                .orElse(request.getRemoteAddr());
        String userId = Optional.ofNullable(request.getHeader("userId"))
                .orElse("anonymous");
        String uri = userDetails.get("uri");
        String ipKey = "rl:ip:"+ip+":"+uri;
        String userKey ="rl:user:"+userId+":"+uri;
        Long userCount = redisTemplate.opsForValue().increment(userKey);
        if (userCount == 1) {
            redisTemplate.expire(ipKey, Duration.ofSeconds(BLOCK_SECONDS));
        }
        Long ipCount = redisTemplate.opsForValue().increment(ipKey);
        if (ipCount == 1) {
            redisTemplate.expire(ipKey, Duration.ofSeconds(BLOCK_SECONDS));
        }
        if(ipCount >=RATE_LIMIT || userCount>=RATE_LIMIT){
            return false;
        }

        redisTemplate.opsForValue().set(ipKey, String.valueOf(ipCount+1), Duration.ofSeconds(BLOCK_SECONDS));
        redisTemplate.opsForValue().set(userKey,String.valueOf(userCount+1), Duration.ofSeconds(BLOCK_SECONDS));
        return true;
    }

    public Map<String,String> extractUserDetails(HttpServletRequest request){
        Map<String,String> userDetails = new HashMap<>();
        userDetails.put("ip",request.getHeader("ip"));
        userDetails.put("userId",request.getHeader("userId"));
        userDetails.put("uri",request.getRequestURI());
        return userDetails;
    }

    private int getCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
