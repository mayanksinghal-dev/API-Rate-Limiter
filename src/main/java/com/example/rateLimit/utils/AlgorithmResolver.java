package com.example.rateLimit.utils;

import com.example.rateLimit.interfaces.RateLimitAlgorithm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AlgorithmResolver {

    private final Map<String, RateLimitAlgorithm> algorithms;

    public AlgorithmResolver(List<RateLimitAlgorithm> algoList){
        this.algorithms = algoList.stream()
                .collect(Collectors.toMap(algo-> algo.getClass().getAnnotation(Component.class).value(), Function.identity()));
    }

    public RateLimitAlgorithm algorithm(String tier){
        return algorithms.get(tier);
    }
}
