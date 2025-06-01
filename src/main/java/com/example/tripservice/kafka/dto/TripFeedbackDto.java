package com.example.tripservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripFeedbackDto {
    private UUID tripId;
    private boolean confirmed; // true = rider said Y, false = rider said N
    private String reason;     // optional reason for dispute
}