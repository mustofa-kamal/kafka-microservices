package com.example.tripservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverEntity {
    @Id
    private Long driverId;
    private String name;
    private String licenseNumber;
}
