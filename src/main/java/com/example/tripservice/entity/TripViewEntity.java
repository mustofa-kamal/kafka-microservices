package com.example.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripViewEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID tripId;

    private Long riderId;
    private Long driverId;

    private String pickupLocation;
    private String dropoffLocation;

    private LocalDateTime requestedTime;
    private LocalDateTime startedTime;
    private LocalDateTime completedTime;

    private String currentStatus;
}
