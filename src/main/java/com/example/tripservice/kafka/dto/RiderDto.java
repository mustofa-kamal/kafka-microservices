package com.example.tripservice.kafka.dto;

import lombok.Data;

@Data
public class RiderDto {
    private Long riderId;
    private String name;
    private String phone;
}
