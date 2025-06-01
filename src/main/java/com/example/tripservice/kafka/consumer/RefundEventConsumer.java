package com.example.tripservice.kafka.consumer;

// Step 6: Implement RefundEventConsumer


import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.RefundEventDto;
import com.example.tripservice.kafka.dto.TripFeedbackDto;
import com.example.tripservice.repository.TripEntityRepository;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefundEventConsumer {

    private final TripEntityRepository tripRepo;
    private final TripStatusHistoryRepository historyRepo;
    private final ObjectMapper objectMapper;

    public RefundEventConsumer(TripEntityRepository tripRepo,TripStatusHistoryRepository historyRepo,
                               ObjectMapper objectMapper) {

        this.tripRepo = tripRepo;
        this.historyRepo = historyRepo;
        this.objectMapper = objectMapper;

    }

    @KafkaListener(topics = "refund-events", groupId = "refund-group")
    public void handleRefundEvent(String message) {
        try {
            RefundEventDto dto = objectMapper.readValue(message, RefundEventDto.class);
            UUID tripId = dto.getTripId();

            Optional<TripEntity> optional = tripRepo.findById(tripId);
            if (optional.isEmpty()) {
                System.out.println("❌ Trip not found for refund: " + tripId);
                return;
            }

            TripEntity entity = optional.get();
            entity.setTripStatus(Status.REFUNDED);
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
                    .tripStatus(Status.REFUNDED)
                    .statusUpdatedAt(LocalDateTime.now())
                    .build();

            historyRepo.save(history);
            System.out.println("💸 Trip refunded and status history recorded for tripId: " + tripId);

        } catch (Exception e) {
            System.err.println("❌ Failed to process refund event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
