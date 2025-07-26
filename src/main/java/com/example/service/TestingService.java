package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class TestingService {

    public void test(String data){
        System.out.println("API tested " + data);
    }
}
