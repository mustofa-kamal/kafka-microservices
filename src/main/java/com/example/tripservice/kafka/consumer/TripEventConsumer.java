package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.repository.TripEntityRepository;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TripEventConsumer {

    private final TripEntityRepository tripRepo;
    private final TripStatusHistoryRepository historyRepo;
    private final ObjectMapper objectMapper;

    public TripEventConsumer(TripEntityRepository tripRepo, TripStatusHistoryRepository historyRepo, ObjectMapper objectMapper) {
        this.tripRepo = tripRepo;
        this.historyRepo = historyRepo;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "trip-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            TripDto dto = objectMapper.readValue(message, TripDto.class);
            if (dto.getTripStatus() == Status.REQUESTED) {
                // Create a new trip entry
                TripEntity newTrip = TripEntity.builder()
                        .tripId(dto.getTripId())
                        .riderId(dto.getRiderId())
                        .pickupLocation(dto.getPickupLocation())
                        .dropoffLocation(dto.getDropoffLocation())
                        .requestedPickupTime(dto.getRequestedPickupTime())
                        .estimatedDropoffTime(dto.getEstimatedDropoffTime())
                        .tripStatus(Status.REQUESTED)
                        .build();

                tripRepo.save(newTrip);

                // Save to history
                TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                        .tripId(newTrip.getTripId())
                        .riderId(newTrip.getRiderId())
                        .driverId(newTrip.getDriverId())
                        .pickupLocation(newTrip.getPickupLocation())
                        .dropoffLocation(newTrip.getDropoffLocation())
                        .requestedPickupTime(newTrip.getRequestedPickupTime())
                        .estimatedDropoffTime(newTrip.getEstimatedDropoffTime())
                        .tripStatus(Status.REQUESTED)
                        .statusUpdatedAt(LocalDateTime.now())
                        .build();

                historyRepo.save(history);
                System.out.println("🚀 New trip created with tripId: " + newTrip.getTripId());

            } else {
                // For DRIVER_ASSIGNED, STARTED, COMPLETED → update existing
                System.out.println("Looking for tripId: " + dto.getTripId());
                Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());

                if (optional.isPresent()) {
                    TripEntity entity = optional.get();
                    entity.setTripStatus(dto.getTripStatus());

                    if (dto.getDriverId() != null) {
                        entity.setDriverId(dto.getDriverId());
                    }
                    // other time updates...
                    tripRepo.save(entity);

                    TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                            .tripId(entity.getTripId())
                            .riderId(entity.getRiderId())
                            .driverId(entity.getDriverId())
                            .pickupLocation(entity.getPickupLocation())
                            .dropoffLocation(entity.getDropoffLocation())
                            .requestedPickupTime(entity.getRequestedPickupTime())
                            .actualPickupTime(entity.getActualPickupTime())
                            .estimatedDropoffTime(entity.getEstimatedDropoffTime())
                            .actualDropoffTime(entity.getActualDropoffTime())
                            .tripStatus(dto.getTripStatus())
                            .statusUpdatedAt(LocalDateTime.now())
                            .build();

                    historyRepo.save(history);
                    System.out.println("✅ Trip updated and history saved: " + dto.getTripId());
                } else {
                    System.out.println("❌ Cannot update — trip not found: " + dto.getTripId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
