package com.example.rateLimit.strategy;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PerIpStrategy implements RateLimitKeyStrategy {

    @Override
    public List<String> resolveKeys(HttpServletRequest request){
        String ip = Optional.ofNullable(request.getHeader("ip"))
                .orElse(request.getRemoteAddr());
        String uri = request.getRequestURI();
        return List.of("rl:ip:"+ip+":"+uri);
    }
}
