package com.example.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStatusHistoryEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID tripId;

    private Long riderId;
    private Long driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime requestedTime;
    private LocalDateTime startedTime;
    private LocalDateTime completedTime;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
