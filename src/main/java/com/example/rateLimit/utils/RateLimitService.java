package com.example.rateLimit.utils;

import com.example.rateLimit.algorithm.FixedWindow;
import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RateLimitService {

//    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    @Autowired
    private StrategyResolver strategyResolver;

    @Autowired
    private FixedWindow fixedWindow;

    @Autowired
    private AlgorithmResolver algorithmResolver;

    public boolean isApprove(HttpServletRequest request) {
        //TODO - use jwt based login and decode and store in redis here. Use the decoded jwt to get user tier.

        RateLimitAlgorithm algorithm = algorithmResolver.algorithm("FREE");
        List<RateLimitKeyStrategy> strategies = strategyResolver.resolveStrategy(request);
        Set<String> allKeys = strategies.stream().map(strategy-> strategy.resolveKeys(request)).collect(Collectors.toSet());
        return algorithm.isApproved(allKeys);
    }
}
