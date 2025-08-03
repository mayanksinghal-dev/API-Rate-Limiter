package com.example.rateLimit.strategy;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PerIpStrategy implements RateLimitKeyStrategy {

    @Override
    public String resolveKeys(HttpServletRequest request){
        String ip = Optional.ofNullable(request.getHeader("ip"))
                .orElse(request.getRemoteAddr());
        String uri = request.getRequestURI();
        return "rl:ip:"+ip+":"+uri;
    }
}
