package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ApiResponse;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse<String> home() {
        return new ApiResponse<>(true, "Welcome to the Expense Splitter API", null);
    }
}
