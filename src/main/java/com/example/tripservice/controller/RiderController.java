package com.example.tripservice.controller;

import com.example.tripservice.entity.RiderEntity;
import com.example.tripservice.repository.RiderEntityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RiderController {

    private final RiderEntityRepository riderRepository;

    public RiderController(RiderEntityRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    @GetMapping("/riders")
    public List<RiderEntity> getAllRiders() {
        return riderRepository.findAll();
    }
}
