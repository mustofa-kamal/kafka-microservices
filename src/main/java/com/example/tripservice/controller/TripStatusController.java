package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripStatusDto;
import com.example.tripservice.kafka.producer.TripStatusProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip-status")
public class TripStatusController {

    private final TripStatusProducer statusProducer;

    public TripStatusController(TripStatusProducer statusProducer) {
        this.statusProducer = statusProducer;
    }

    @PostMapping
    public String updateTripStatus(@RequestBody TripStatusDto dto) {
        statusProducer.sendTripStatus(dto);
        return "✅ Trip status event sent: " + dto.getStatus();
    }
}
