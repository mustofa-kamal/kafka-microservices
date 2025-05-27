package com.example.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private LocalDateTime startTime;
    private LocalDateTime estimatedArrivalTime;

    private String status;
}
