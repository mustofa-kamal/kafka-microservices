package com.example.tripservice.kafka.dto;

import com.example.tripservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDriverAssignedDto {
    private UUID tripId;
    private UUID driverId;
    private Status status;  // ✅ Use enum instead of String
}
