package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.RiderEntity;
import com.example.tripservice.kafka.dto.RiderDto;
import com.example.tripservice.repository.RiderEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RiderEventConsumer {

    private final RiderEntityRepository riderRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RiderEventConsumer(RiderEntityRepository riderRepo) {
        this.riderRepo = riderRepo;
    }

    @KafkaListener(topics = "rider-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            RiderDto riderDto = objectMapper.readValue(message, RiderDto.class);

            RiderEntity ride= RiderEntity.builder()
                    .riderId(riderDto.getRiderId())
                    .name(riderDto.getName())
                    .phone(riderDto.getPhone())
                    .build();

            riderRepo.save(ride);

            System.out.println("✅ Rider info saved: " + ride);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
