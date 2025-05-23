package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.RiderDto;
import com.example.tripservice.kafka.producer.RiderEventProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider-events")
public class RiderEventController {

    private final RiderEventProducer producer;

    public RiderEventController(RiderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String sendRiderEvent(@RequestBody RiderDto event) {
        producer.sendRiderEvent(event);
        return "✅ Rider event sent for riderId: " + event.getRiderId();
    }
}
