package com.example.rateLimit.utils;

import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import com.example.rateLimit.interfaces.RateLimitKeyStrategy;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RateLimitService {

    @Autowired
    private StrategyResolver strategyResolver;

    @Autowired
    private AlgorithmResolver algorithmResolver;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private JwtDecode jwtDecode;

    public boolean isApprove(HttpServletRequest request) {
        //TODO - use jwt based login and decode and store in redis here. Use the decoded jwt to get user tier.
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidGllciI6IlBSRU1JVU0iLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.D4T6uHhgRmeOMA-3oyCw9aUAHoGroMA74RF_y9hlG2ag4bfvWwsImG9egiwz_ykabU8vIi7Vb1A6sZTgu9OkuciAUXW_e7P1Rk_qCt2WUVX6dPMyMMWG-IOQbfyhBY9hi4aC6pkrIs6uMz3A8QdZBXXHv1PQ3JYsOWoFMGlZGEFvJzajiLptBZFXy_F8MZQtQ-Yv-IqePunLUL46hYwunNJdeuzpkSWmZE1-MRYK6QFMCz54-katJtNCzsMLizXn0UpiRgwcBcY4m1ZmM5GylQRPitbUa536jMQ5c9osnjpZEGgZ1wqxHf4m6pjlN5_e-Ikv8_dlyyPRIxTeZ2Iucg";
        Jws<Claims> jws = jwtDecode.parse(token);
        RateLimitAlgorithm algorithm = algorithmResolver.algorithm(jws.getPayload().get("tier").toString());
        List<RateLimitKeyStrategy> strategies = strategyResolver.resolveStrategy(request);
        Set<String> allKeys = strategies.stream().map(strategy -> strategy.resolveKeys(request)).collect(Collectors.toSet());
        return algorithm.isApproved(allKeys);
    }
}
