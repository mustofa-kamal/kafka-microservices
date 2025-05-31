package com.example.tripservice.controller;

import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripQueryController {

    private final TripStatusHistoryRepository historyRepo;

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TripStatusHistoryEntity>> getTripHistory(@PathVariable UUID id) {
        List<TripStatusHistoryEntity> history = historyRepo.findByTripIdOrderByStatusUpdatedAtAsc(id);
        return ResponseEntity.ok(history);
    }
}

