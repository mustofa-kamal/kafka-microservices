package com.example.tripservice.kafka.dto;

import com.example.tripservice.entity.Status;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
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

    private LocalDateTime requestedPickupTime;// when request was made
    private LocalDateTime driverAssignedTime; // 🆕 when driver is assigned
    private LocalDateTime actualPickupTime;   // 🆕 when trip starts
    private LocalDateTime estimatedDropoffTime;// estimated dropoff time
    private LocalDateTime actualDropoffTime;  // when trip completes




    @Enumerated(EnumType.STRING)
    private Status tripStatus;

    @CreationTimestamp
    private LocalDateTime statusUpdatedAt;

    // 🆕 Cancellation info
    private String cancellationReason;
    private String cancelledBy;
}
