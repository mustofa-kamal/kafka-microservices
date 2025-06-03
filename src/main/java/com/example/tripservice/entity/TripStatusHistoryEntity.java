package com.example.tripservice.entity;
import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private String cancellationReason;
    private String cancelledBy;





    @Enumerated(EnumType.STRING)
    private Status tripStatus;

    @CreationTimestamp
    private LocalDateTime statusUpdatedAt;
}
