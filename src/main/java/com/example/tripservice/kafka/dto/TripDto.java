package com.example.tripservice.kafka.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripDto {
    private Long tripId;
    private Long riderId;
    private Long driverId;

    private String pickupLocation;
    private String dropoffLocation;

    private LocalDateTime startTime;
    private LocalDateTime estimatedArrivalTime;

    private String status; // REQUESTED, IN_PROGRESS, COMPLETED
}
