package com.example.tripservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestedDto {
    private UUID tripId;
    private Long riderId;
    private String pickupLocation;
    private String dropoffLocation;
}
