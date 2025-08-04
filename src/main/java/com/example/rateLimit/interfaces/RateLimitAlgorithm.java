package com.example.rateLimit.interfaces;


import java.util.Set;

public interface RateLimitAlgorithm {
    boolean isApproved(Set<String> keys);
}
