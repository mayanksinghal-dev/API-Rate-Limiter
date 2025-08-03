package com.example.rateLimit.strategy;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PerUserStrategy implements RateLimitKeyStrategy {

    @Override
    public String resolveKeys(HttpServletRequest request){
        String user = Optional.ofNullable(request.getHeader("user")).orElse(request.getRemoteUser());
        String uri = request.getRequestURI();
        return "rl:user:"+user+":"+uri;
    }
}
