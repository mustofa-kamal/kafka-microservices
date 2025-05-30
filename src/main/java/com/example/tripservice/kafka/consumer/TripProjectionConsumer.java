package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.entity.TripViewEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.dto.TripStatusDto;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.example.tripservice.repository.TripViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripProjectionConsumer {

    private final TripViewRepository tripViewRepo;
    private final TripStatusHistoryRepository statusRepo;
    private final ObjectMapper objectMapper;

    public TripProjectionConsumer(TripViewRepository tripViewRepo, TripStatusHistoryRepository statusRepo, ObjectMapper objectMapper) {
        this.tripViewRepo = tripViewRepo;
        this.statusRepo = statusRepo;
        this.objectMapper =objectMapper;

    }

    // 🔹 Handles trip creation from controller
    @KafkaListener(topics = "trip-events-v2", groupId = "trip-view-service")
    public void handleTripEvent(String message) {
        try {
            TripDto tripDto = objectMapper.readValue(message, TripDto.class);

            TripViewEntity view = TripViewEntity.builder()
                    .tripId(tripDto.getTripId())
                    .riderId(tripDto.getRiderId())
                    .driverId(tripDto.getDriverId())

                    .pickupLocation(tripDto.getPickupLocation())
                    .dropoffLocation(tripDto.getDropoffLocation())
                    .requestedPickupTime(tripDto.getRequestedPickupTime())
                    .actualPickupTime(tripDto.getActualPickupTime())
                    .estimatedDropoffTime(tripDto.getEstimatedDropoffTime())
                    .actualDropoffTime(tripDto.getActualDropoffTime())
                    .tripStatus(tripDto.getTripStatus())
                    .build();

            tripViewRepo.save(view);
            System.out.println("📌 TripView created: " + tripDto.getTripId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Handles status updates (REQUESTED → DRIVER_ASSIGNED → STARTED, etc.)
    @KafkaListener(topics = "trip-status-events", groupId = "trip-view-service")
    public void handleStatusChange(String message) {
        try {
            TripStatusDto statusDto = objectMapper.readValue(message, TripStatusDto.class);
            Optional<TripViewEntity> optView = tripViewRepo.findById(statusDto.getTripId());

            if (optView.isPresent()) {
                TripViewEntity view = optView.get();
                view.setTripStatus(statusDto.getStatus());

                switch (statusDto.getStatus()) {
                    case DRIVER_ASSIGNED -> view.setDriverAssignedTime(statusDto.getStatusUpdatedAt());
                    case STARTED         -> view.setActualPickupTime(statusDto.getStatusUpdatedAt());
                    case COMPLETED      -> view.setActualDropoffTime(statusDto.getStatusUpdatedAt());
                }


                tripViewRepo.save(view);
                System.out.println("🔄 TripView updated to " + statusDto.getStatus());

                // Save history
                TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                        .tripId(view.getTripId())
                        .riderId(view.getRiderId())
                        .driverId(view.getDriverId())
                        .pickupLocation(view.getPickupLocation())
                        .dropoffLocation(view.getDropoffLocation())
                        .requestedPickupTime(view.getRequestedPickupTime())
                        .actualPickupTime(view.getActualPickupTime())
                        .estimatedDropoffTime(view.getEstimatedDropoffTime())
                        .actualDropoffTime(view.getActualDropoffTime())
                        .tripStatus(statusDto.getStatus())
                        .build();

                statusRepo.save(history);
                System.out.println("📚 Status history recorded: " + statusDto.getStatus());
            } else {
                System.out.println("⚠️ TripView not found for ID " + statusDto.getTripId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

