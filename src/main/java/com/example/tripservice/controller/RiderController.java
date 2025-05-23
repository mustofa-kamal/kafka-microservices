package com.example.tripservice.controller;

import com.example.tripservice.entity.RiderEntity;
import com.example.tripservice.repository.RiderInfoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RiderController {

    private final RiderInfoRepository riderRepository;

    public RiderController(RiderInfoRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    @GetMapping("/riders")
    public List<RiderEntity> getAllRiders() {
        return riderRepository.findAll();
    }
}
