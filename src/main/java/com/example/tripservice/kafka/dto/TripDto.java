package com.example.tripservice.kafka.dto;

import com.example.tripservice.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripDto {
    private UUID tripId;
    private Long riderId;
    private Long driverId;

    private String pickupLocation;
    private String dropoffLocation;

    private LocalDateTime requestedPickupTime;
    private LocalDateTime actualPickupTime;


    private LocalDateTime estimatedDropoffTime;
    private LocalDateTime actualDropoffTime;


    @Enumerated(EnumType.STRING)
    private Status tripStatus;

    @CreationTimestamp
    private LocalDateTime statusUpdatedAt;
}
