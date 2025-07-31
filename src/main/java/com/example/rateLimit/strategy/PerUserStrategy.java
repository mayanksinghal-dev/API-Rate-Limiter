package com.example.rateLimit.strategy;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PerUserStrategy implements RateLimitKeyStrategy {

    @Override
    public List<String> resolveKeys(HttpServletRequest request){
        String user = Optional.ofNullable(request.getHeader("user")).orElse(request.getRemoteUser());
        String uri = request.getRequestURI();
        return List.of("rl:user:"+user+":"+uri);
    }
}
