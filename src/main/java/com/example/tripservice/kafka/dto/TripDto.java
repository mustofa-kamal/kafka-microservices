package com.example.tripservice.kafka.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripDto {
    private UUID tripId;
    private Long riderId;
    private Long driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime startTime;
    private LocalDateTime estimatedArrivalTime;
    private String status;
}
