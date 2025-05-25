package com.example.tripservice.kafka.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripStatusDto {
    private UUID tripId;
    private String status; // e.g., REQUESTED, STARTED, COMPLETED, CANCELLED
    private LocalDateTime timestamp;
}
