package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.producer.TripCompensationProducer;
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
    private final TripCompensationProducer compensationProducer;

    public TripEventConsumer(TripEntityRepository tripRepo, TripStatusHistoryRepository historyRepo,
                             ObjectMapper objectMapper, TripCompensationProducer compensationProducer) {
        this.tripRepo = tripRepo;
        this.historyRepo = historyRepo;
        this.objectMapper = objectMapper;
        this.compensationProducer = compensationProducer;
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

            } else if (dto.getTripStatus() == Status.DRIVER_ASSIGNED) {

                Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());

                if (optional.isPresent()) {
                    TripEntity entity = optional.get();
                    entity.setTripStatus(dto.getTripStatus());
                    entity.setDriverAssignedTime(dto.getDriverAssignedTime());
                    entity.setDriverId(dto.getDriverId());


                    // other time updates...
                    tripRepo.save(entity);

                    TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                            .tripId(entity.getTripId())
                            .riderId(entity.getRiderId())
                            .driverId(entity.getDriverId())
                            .pickupLocation(entity.getPickupLocation())
                            .driverAssignedTime(entity.getDriverAssignedTime())
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
            } else if (dto.getTripStatus() == Status.STARTED) {
                Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
                if (optional.isPresent()) {
                    TripEntity entity = optional.get();
                    entity.setTripStatus(dto.getTripStatus());
                    entity.setActualPickupTime(dto.getActualPickupTime());







                    tripRepo.save(entity);

                    TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                            .tripId(entity.getTripId())
                            .riderId(entity.getRiderId())
                            .driverId(entity.getDriverId())
                            .driverAssignedTime(entity.getDriverAssignedTime())
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
                    System.out.println("✅ Trip updated to STARTED and history saved: " + dto.getTripId());
                }
            } else if (dto.getTripStatus() == Status.COMPLETED) {
                Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
                if (optional.isPresent()) {
                    TripEntity entity = optional.get();
                    entity.setTripStatus(dto.getTripStatus());
                    entity.setActualDropoffTime(dto.getActualDropoffTime());






                    tripRepo.save(entity);

                    TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                            .tripId(entity.getTripId())
                            .riderId(entity.getRiderId())
                            .driverId(entity.getDriverId())
                            .driverAssignedTime(entity.getDriverAssignedTime())
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
                    System.out.println("✅ Trip updated to COMPLETED and history saved: " + dto.getTripId());
                }


            }
            else if (dto.getTripStatus() == Status.CANCELLED) {
                Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
                if (optional.isPresent()) {
                    TripEntity entity = optional.get();
                    entity.setTripStatus(Status.CANCELLED);



                    entity.setCancellationReason(dto.getCancellationReason());
                    entity.setCancelledBy(dto.getCancelledBy());

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
                            .tripStatus(Status.CANCELLED)
                            .cancellationReason(dto.getCancellationReason())   // 🆕
                            .cancelledBy(dto.getCancelledBy())
                            .statusUpdatedAt(LocalDateTime.now())
                            .build();

                    historyRepo.save(history);
                    System.out.println("⚠️ Trip CANCELLED and history saved: " + dto.getTripId());
                } else {
                    System.out.println("❌ Cannot cancel — trip not found: " + dto.getTripId());
                }
            }



        } catch (Exception e) {
            e.printStackTrace();

            try {
                TripDto dto = objectMapper.readValue(message, TripDto.class);
                if (dto.getTripStatus() == Status.COMPLETED) {
                    Status rollbackTo = Status.STARTED;
                    String reason = "Failed to complete trip — triggering compensation to STARTED.";
                    compensationProducer.sendCompensation(dto.getTripId(), rollbackTo, reason);
                } else if (dto.getTripStatus() == Status.STARTED) {
                    Status rollbackTo = Status.DRIVER_ASSIGNED;
                    String reason = "Failed to start trip — rolling back to DRIVER_ASSIGNED.";
                    compensationProducer.sendCompensation(dto.getTripId(), rollbackTo, reason);
                } else if (dto.getTripStatus() == Status.DRIVER_ASSIGNED) {
                    Status rollbackTo = Status.REQUESTED;
                    String reason = "Failed to assign driver — rolling back to REQUESTED.";
                    compensationProducer.sendCompensation(dto.getTripId(), rollbackTo, reason);
                }
            } catch (Exception parseEx) {
                System.err.println("❌ Failed to parse original DTO in compensation handler.");
                parseEx.printStackTrace();
            }
        }

    }
}
