package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.TripCompensationProducer;
import com.example.tripservice.repository.TripEntityRepository;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TripEventConsumer {

    private final TripEntityRepository tripRepo;
    private final TripStatusHistoryRepository historyRepo;
    private final ObjectMapper objectMapper;
    private final TripCompensationProducer compensationProducer;

    public TripEventConsumer(TripEntityRepository tripRepo,
                             TripStatusHistoryRepository historyRepo,
                             ObjectMapper objectMapper,
                             TripCompensationProducer compensationProducer) {
        this.tripRepo = tripRepo;
        this.historyRepo = historyRepo;
        this.objectMapper = objectMapper;
        this.compensationProducer = compensationProducer;
    }

    @KafkaListener(topics = "trip-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            TripDto dto = objectMapper.readValue(message, TripDto.class);
            switch (dto.getTripStatus()) {
                case REQUESTED -> handleRequested(dto);
                case DRIVER_ASSIGNED, STARTED, COMPLETED, CANCELLED -> updateTrip(dto);
                default -> System.out.println("⚠️ Unknown trip status received: " + dto.getTripStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleCompensation(message);
        }
    }

    private void handleRequested(TripDto dto) {
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
        historyRepo.save(buildTripHistory(newTrip, Status.REQUESTED, null));
        System.out.println("🚀 New trip created with tripId: " + newTrip.getTripId());
    }

    private void updateTrip(TripDto dto){
        Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
        if (optional.isEmpty()) {
            System.out.println("❌ Trip not found: " + dto.getTripId());
            return;
        }

        TripEntity entity = optional.get();
        entity.setTripStatus(dto.getTripStatus());

        switch (dto.getTripStatus()) {
            case DRIVER_ASSIGNED -> {

                entity.setDriverId(dto.getDriverId());
                entity.setDriverAssignedTime(dto.getDriverAssignedTime());
            }
            case STARTED ->  entity.setActualPickupTime(dto.getActualPickupTime());
            case COMPLETED -> entity.setActualDropoffTime(dto.getActualDropoffTime());
            case CANCELLED -> {
                entity.setCancellationReason(dto.getCancellationReason());
                entity.setCancelledBy(dto.getCancelledBy());
            }
        }

        tripRepo.save(entity);
        historyRepo.save(buildTripHistory(entity, dto.getTripStatus(), dto));
        System.out.println("✅ Trip updated to " + dto.getTripStatus() + " and history saved: " + dto.getTripId());
    }

    private TripStatusHistoryEntity buildTripHistory(TripEntity entity, Status status, TripDto dto) {
        TripStatusHistoryEntity.TripStatusHistoryEntityBuilder builder = TripStatusHistoryEntity.builder()
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
                .tripStatus(status)
                .statusUpdatedAt(LocalDateTime.now());

        if (status == Status.CANCELLED && dto != null) {
            builder.cancellationReason(dto.getCancellationReason())
                    .cancelledBy(dto.getCancelledBy());
        }

        return builder.build();
    }

    private void handleCompensation(String message) {
        try {
            TripDto dto = objectMapper.readValue(message, TripDto.class);
            Status rollbackTo = null;
            String reason = null;

            switch (dto.getTripStatus()) {
                case COMPLETED -> {
                    rollbackTo = Status.STARTED;
                    reason = "Failed to complete trip — triggering compensation to STARTED.";
                }
                case STARTED -> {
                    rollbackTo = Status.DRIVER_ASSIGNED;
                    reason = "Failed to start trip — rolling back to DRIVER_ASSIGNED.";
                }
                case DRIVER_ASSIGNED -> {
                    rollbackTo = Status.REQUESTED;
                    reason = "Failed to assign driver — rolling back to REQUESTED.";
                }
            }

            if (rollbackTo != null) {
                compensationProducer.sendCompensation(dto.getTripId(), rollbackTo, reason);
            }
        } catch (Exception parseEx) {
            System.err.println("❌ Failed to parse original DTO in compensation handler.");
            parseEx.printStackTrace();
        }
    }
}
