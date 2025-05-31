package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.TripCompensationDto;
import com.example.tripservice.repository.TripEntityRepository;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TripCompensationConsumer {

    private final TripEntityRepository tripRepo;
    private final TripStatusHistoryRepository historyRepo;
    private final ObjectMapper objectMapper;

    public TripCompensationConsumer(TripEntityRepository tripRepo,
                                    TripStatusHistoryRepository historyRepo,
                                    ObjectMapper objectMapper) {

        this.tripRepo = tripRepo;
        this.historyRepo=historyRepo;
        this.objectMapper = objectMapper;

    }

    @KafkaListener(topics = "trip-events-compensate", groupId = "trip-saga")
    public void handleCompensation(String message) {
        try {
            TripCompensationDto dto = objectMapper.readValue(message, TripCompensationDto.class);
            System.out.println("🧹 Handling compensation for: " + dto.getTripId() + " → " + dto.getRevertToStatus());

            Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
            if (optional.isPresent()) {
                TripEntity entity = optional.get();
                Status rollbackStatus = Status.valueOf(dto.getRevertToStatus().toString());
                entity.setTripStatus(rollbackStatus);
                tripRepo.save(entity);

                TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                        .tripId(entity.getTripId())
                        .riderId(entity.getRiderId())
                        .driverId(entity.getDriverId())
                        .pickupLocation(entity.getPickupLocation())
                        .dropoffLocation(entity.getDropoffLocation())
                        .requestedPickupTime(entity.getRequestedPickupTime())
                        .driverAssignedTime(entity.getDriverAssignedTime())
                        .actualPickupTime(entity.getActualPickupTime())
                        .estimatedDropoffTime(entity.getEstimatedDropoffTime())
                        .actualDropoffTime(entity.getActualDropoffTime())
                        .tripStatus(rollbackStatus)
                        .statusUpdatedAt(LocalDateTime.now())
                        .cancellationReason(dto.getReason()) // optional: store compensation reason here
                        .build();

                historyRepo.save(history);

                System.out.println("↩️ Compensation applied: trip " + dto.getTripId() + " reverted to " + rollbackStatus);
            } else {
                System.out.println("❌ Trip not found for compensation: " + dto.getTripId());
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
