// Step 4: Define RefundEventDto
package com.example.tripservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundEventDto {
    private UUID tripId;
    private String reason;
}