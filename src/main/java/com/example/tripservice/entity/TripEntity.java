package com.example.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripEntity {

    @Id
    private Long tripId;

    private Long riderId;
    private Long driverId;

    private String pickupLocation;
    private String dropoffLocation;

    private LocalDateTime startTime;
    private LocalDateTime estimatedArrivalTime;

    private String status;
}
