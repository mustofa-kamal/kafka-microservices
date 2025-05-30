package com.example.tripservice.controller;

import com.example.tripservice.entity.Status;
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
        UUID tripId = dto.getTripId()==null?UUID.randomUUID():dto.getTripId();
        dto.setTripId(tripId);

        dto.setRequestedPickupTime(LocalDateTime.now());

        // For learning/testing, we can hardcode estimatedDropoffTime if not set
        if (dto.getEstimatedDropoffTime() == null) {
            dto.setEstimatedDropoffTime(dto.getRequestedPickupTime().plusMinutes(30));
        }

        tripEventProducer.sendTripEvent(dto);
        return "Trip requested with tripId: " + tripId;
    }
}
