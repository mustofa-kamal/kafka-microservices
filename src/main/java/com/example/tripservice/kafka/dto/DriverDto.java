package com.example.tripservice.kafka.dto;

import lombok.Data;

@Data
public class DriverDto {
    private Long driverId;
    private String name;
    private String licenseNumber;
}
