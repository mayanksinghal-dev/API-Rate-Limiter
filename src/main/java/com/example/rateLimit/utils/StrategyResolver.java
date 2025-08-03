package com.example.rateLimit.utils;

import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StrategyResolver {

    private final Map<String, RateLimitKeyStrategy> strategies;

    private final Map<String, List<String>> routeStrategyMap = Map.of(
            "/auth", List.of("PerIpStrategy"),
            "/user", List.of("PerIpStrategy", "PerUserStrategy"),
            "/health", List.of("PerIpStrategy", "PerUserStrategy")
    );

    public StrategyResolver(List<RateLimitKeyStrategy> strategyList){
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s-> s.getClass().getSimpleName(), Function.identity()));
    }

    public List<RateLimitKeyStrategy> resolveStrategy(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (Map.Entry<String, List<String>> entry : routeStrategyMap.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue().stream()
                        .map(strategies::get)
                        .collect(Collectors.toList());
            }
        }
        return List.of(strategies.get("PerUserStrategy"));
    }
}
