package com.example.tripservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripCompensationDto {
    private UUID tripId;
    private String revertToStatus;  // ✅ use String instead of Status
    private String reason;
}
