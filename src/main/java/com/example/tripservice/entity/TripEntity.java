package com.example.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
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

    private String cancellationReason;
    private String cancelledBy;



}
