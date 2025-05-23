package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.DriverDto;
import com.example.tripservice.kafka.producer.DriverEventProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver-events")
public class DriverEventController {

    private final DriverEventProducer producer;

    public DriverEventController(DriverEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String sendDriverEvent(@RequestBody DriverDto event) {
        producer.sendDriverEvent(event);
        return "✅ Driver event sent for driverId: " + event.getDriverId();
    }
}
