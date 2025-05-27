package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.TripEventProducer;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/trip-events")
public class TripEventController {

    private final TripEventProducer tripEventProducer;

    public TripEventController(TripEventProducer tripEventProducer) {
        this.tripEventProducer = tripEventProducer;
    }

    @PostMapping
    public String createTrip(@RequestBody TripDto dto) {
        UUID tripId = UUID.randomUUID();  // ✅ Generate UUID in controller
        dto.setTripId(tripId);
        dto.setStatus("REQUESTED"); // or any default
        dto.setStartTime(LocalDateTime.now());

        tripEventProducer.sendTripEvent(dto);
        return "Trip requested with tripId: " + tripId;
    }
}
