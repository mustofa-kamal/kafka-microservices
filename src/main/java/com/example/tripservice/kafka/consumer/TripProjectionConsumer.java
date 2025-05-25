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
import java.util.UUID;

@Service
public class TripProjectionConsumer {

    private final TripViewRepository tripViewRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TripStatusHistoryRepository statusRepo;

    public TripProjectionConsumer(TripViewRepository repo) {
        this.tripViewRepo = repo;
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
                    .requestedTime(tripDto.getStartTime()) // assuming trip creation time
                    .currentStatus("REQUESTED")
                    .build();

            tripViewRepo.save(view);
            System.out.println("✅ Trip projection created: " + view.getTripId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "trip-status-events", groupId = "trip-view-service")
    public void handleTripStatus(String message) {
        try {
            TripStatusDto tripStatusDto = objectMapper.readValue(message, TripStatusDto.class);
            Optional<TripViewEntity> optional = tripViewRepo.findById(tripStatusDto.getTripId());

            if (optional.isPresent()) {
                TripViewEntity view = optional.get();
                view.setCurrentStatus(tripStatusDto.getStatus());

                switch (tripStatusDto.getStatus()) {
                    case "STARTED" -> view.setStartedTime(tripStatusDto.getTimestamp());
                    case "COMPLETED" -> view.setCompletedTime(tripStatusDto.getTimestamp());
                }

                tripViewRepo.save(view);
                System.out.println("🔄 Trip tripStatusDto updated: " + tripStatusDto.getStatus() + " for tripId " + tripStatusDto.getTripId());
            } else {
                System.out.println("⚠️ TripView not found for tripId: " + tripStatusDto.getTripId());
            }


            TripViewEntity view = tripViewRepo.findById(tripStatusDto.getTripId()).orElse(null);
            if (view != null) {
                TripStatusHistoryEntity snapshot = TripStatusHistoryEntity.builder()
                        .id(UUID.randomUUID())
                        .tripId(view.getTripId())
                        .riderId(view.getRiderId())
                        .driverId(view.getDriverId())
                        .pickupLocation(view.getPickupLocation())
                        .dropoffLocation(view.getDropoffLocation())
                        .requestedTime(view.getRequestedTime())
                        .startedTime(view.getStartedTime())
                        .completedTime(view.getCompletedTime())
                        .status(tripStatusDto.getStatus())
                        .timestamp(tripStatusDto.getTimestamp())
                        .build();

                statusRepo.save(snapshot);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
