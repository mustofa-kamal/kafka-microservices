package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.DriverEntity;
import com.example.tripservice.kafka.dto.DriverDto;
import com.example.tripservice.repository.DriverInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverEventConsumer {

    private final DriverInfoRepository driverRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DriverEventConsumer(DriverInfoRepository driverRepo) {
        this.driverRepo = driverRepo;
    }

    @KafkaListener(topics = "driver-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            DriverDto driverDto = objectMapper.readValue(message, DriverDto.class);

            DriverEntity driver = DriverEntity.builder()
                    .driverId(driverDto.getDriverId())
                    .name(driverDto.getName())
                    .licenseNumber(driverDto.getLicenseNumber())
                    .build();

            driverRepo.save(driver);

            System.out.println("✅ Driver info saved: " + driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
