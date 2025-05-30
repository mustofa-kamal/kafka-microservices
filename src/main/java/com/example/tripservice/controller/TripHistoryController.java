package com.example.tripservice.controller;

import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
public class TripHistoryController {

    private final TripStatusHistoryRepository historyRepo;

    public TripHistoryController(TripStatusHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    @GetMapping("/{tripId}/status-history")
    public List<TripStatusHistoryEntity> getTripStatusHistory(@PathVariable UUID tripId) {
        return historyRepo.findByTripId(tripId);
    }
}
