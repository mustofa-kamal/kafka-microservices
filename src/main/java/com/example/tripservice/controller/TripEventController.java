package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.TripEventProducer;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trip-events")
public class TripEventController {

    private final TripEventProducer producer;

    public TripEventController(TripEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String sendTripEvent(@RequestBody TripDto event) {
        producer.sendTripEvent(event);
        return "✅ Trip event published successfully";
    }
}
