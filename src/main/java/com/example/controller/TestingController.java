package com.example.controller;

import com.example.service.TestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController()
@RequestMapping("/health")
@RequiredArgsConstructor()
public class TestingController {

    @Autowired
    private TestingService testingService;

    @GetMapping("/test")
    public void test(
            @RequestBody String data,
            @RequestHeader Map<String, String> headers
    ){
        this.testingService.test(data);
    }
}
