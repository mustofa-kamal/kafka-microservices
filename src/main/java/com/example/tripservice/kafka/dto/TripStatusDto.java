package com.example.tripservice.kafka.dto;

import com.example.tripservice.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripStatusDto {
    private UUID tripId;
    private Status status; // REQUESTED, DRIVER_ASSIGNED, STARTED, COMPLETED
    private LocalDateTime statusUpdatedAt;
    private LocalDateTime estimatedDropoffTime; // ✅ Add this line
}
