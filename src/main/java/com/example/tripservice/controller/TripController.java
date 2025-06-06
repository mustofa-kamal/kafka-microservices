package com.example.tripservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")  // ✅ keep /api here
public class TripController {

    @GetMapping("/hello")
    public String hello() {
        return "Trip service is up!";
    }
}
