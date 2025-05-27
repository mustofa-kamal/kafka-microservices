package com.example.tripservice.kafka.consumer;

import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.repository.TripEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TripEventConsumer {

    private final TripEntityRepository tripRepo;
    private final ObjectMapper objectMapper;

    public TripEventConsumer(TripEntityRepository tripRepo) {
        this.tripRepo = tripRepo;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "trip-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            TripDto dto = objectMapper.readValue(message, TripDto.class);

            TripEntity entity = TripEntity.builder()
                    .tripId(dto.getTripId())
                    .riderId(dto.getRiderId())
                    .driverId(dto.getDriverId())
                    .pickupLocation(dto.getPickupLocation())
                    .dropoffLocation(dto.getDropoffLocation())
                    .startTime(dto.getStartTime())
                    .estimatedArrivalTime(dto.getEstimatedArrivalTime())
                    .status(dto.getStatus())
                    .build();

            tripRepo.save(entity);
            System.out.println("✅ Trip saved with tripId: " + entity.getTripId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
