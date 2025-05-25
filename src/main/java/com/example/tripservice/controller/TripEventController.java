package com.example.tripservice.controller;

import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.TripEventProducer;
import com.example.tripservice.repository.TripEntityRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip-events")
public class TripEventController {

    private final TripEntityRepository tripEntityRepository;
    private final TripEventProducer tripEventProducer;

    public TripEventController(TripEntityRepository tripEntityRepository, TripEventProducer tripEventProducer) {
        this.tripEntityRepository = tripEntityRepository;
        this.tripEventProducer = tripEventProducer;
    }

    @PostMapping
    public String createTrip(@RequestBody TripDto dto) {
        TripEntity entity = TripEntity.builder()
                .riderId(dto.getRiderId())
                .driverId(dto.getDriverId())
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .startTime(dto.getStartTime())
                .estimatedArrivalTime(dto.getEstimatedArrivalTime())
                .status("REQUESTED")
                .build();

        tripEntityRepository.save(entity); // UUID generated here

        TripDto kafkaDto = new TripDto();
        kafkaDto.setTripId(entity.getTripId());
        kafkaDto.setRiderId(entity.getRiderId());
        kafkaDto.setDriverId(entity.getDriverId());
        kafkaDto.setPickupLocation(entity.getPickupLocation());
        kafkaDto.setDropoffLocation(entity.getDropoffLocation());
        kafkaDto.setStartTime(entity.getStartTime());
        kafkaDto.setEstimatedArrivalTime(entity.getEstimatedArrivalTime());
        kafkaDto.setStatus(entity.getStatus());

        tripEventProducer.sendTripEvent(kafkaDto);

        return "✅ Trip created with ID: " + entity.getTripId();
    }
}
