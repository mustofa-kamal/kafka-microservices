package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.dto.TripRequestedDto;
import com.example.tripservice.kafka.producer.TripEventProducer;
import com.example.tripservice.kafka.producer.TripRequestedProducer;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.repository.TripEntityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trip-events")
public class TripEventController {

    private final TripEntityRepository tripRepo;
    private final TripEventProducer tripEventProducer;
    private final TripRequestedProducer tripRequestedProducer;

    public TripEventController(
            TripEntityRepository tripRepo,
            TripEventProducer tripEventProducer,
            TripRequestedProducer tripRequestedProducer
    ) {
        this.tripRepo = tripRepo;
        this.tripEventProducer = tripEventProducer;
        this.tripRequestedProducer = tripRequestedProducer;
    }

    @PostMapping
    public String createTrip(@RequestBody TripDto dto) {
        // Generate UUID in app layer if not provided
        UUID tripId = UUID.randomUUID();
        dto.setTripId(tripId);

        // Save tripEntity to DB (source of truth)
        TripEntity entity = TripEntity.builder()
                .tripId(tripId)
                .riderId(dto.getRiderId())
                .driverId(dto.getDriverId())
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .startTime(dto.getStartTime())
                .estimatedArrivalTime(dto.getEstimatedArrivalTime())
                .status(dto.getStatus())
                .build();

        tripRepo.save(entity);

        // 🔁 1. Full trip snapshot
        tripEventProducer.sendTripEvent(dto);

        // 🚦 2. Saga trigger event
        TripRequestedDto requestedEvent = new TripRequestedDto(
                dto.getTripId(),
                dto.getRiderId(),
                dto.getPickupLocation(),
                dto.getDropoffLocation()
        );
        tripRequestedProducer.sendTripRequested(requestedEvent);

        return "✅ Trip created and events emitted. Trip ID: " + tripId;
    }
}
