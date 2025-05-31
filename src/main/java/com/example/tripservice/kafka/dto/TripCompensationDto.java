package com.example.tripservice.kafka.dto;

import com.example.tripservice.entity.Status;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripCompensationDto {
    private UUID tripId;
    private Status revertToStatus;
    private String reason;
}
