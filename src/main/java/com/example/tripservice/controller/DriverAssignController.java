package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.DriverAssignedProducer;
import com.example.tripservice.entity.Status;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver-assign")
public class DriverAssignController {

    private final DriverAssignedProducer producer;

    public DriverAssignController(DriverAssignedProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String assignDriver(@RequestBody TripDto dto) {
        dto.setTripStatus(Status.DRIVER_ASSIGNED);
        producer.sendDriverAssignedEvent(dto);
        return "🚗 Driver assigned event sent for tripId: " + dto.getTripId();
    }
}
