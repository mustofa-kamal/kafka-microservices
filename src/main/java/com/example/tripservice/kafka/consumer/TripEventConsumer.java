package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.repository.TripInfoRepository;
import com.example.tripservice.repository.RiderInfoRepository;
import com.example.tripservice.repository.DriverInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TripEventConsumer {

    private final TripInfoRepository tripRepo;
    private final RiderInfoRepository riderRepo;
    private final DriverInfoRepository driverRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TripEventConsumer(
            TripInfoRepository tripRepo,
            RiderInfoRepository riderRepo,
            DriverInfoRepository driverRepo
    ) {
        this.tripRepo = tripRepo;
        this.riderRepo = riderRepo;
        this.driverRepo = driverRepo;

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    @KafkaListener(topics = "trip-events-v2", groupId = "trip-service")
    public void consume(String message) {
        try {
            TripDto tripDto = objectMapper.readValue(message, TripDto.class);

            // Validate rider and driver existence
            boolean riderExists = riderRepo.existsById(tripDto.getRiderId());
            boolean driverExists = driverRepo.existsById(tripDto.getDriverId());

            if (!riderExists || !driverExists) {
                System.out.println("❌ Invalid TripEvent: Missing rider or driver. Skipping tripId ");
                return;
            }

            TripEntity trip = TripEntity.builder()
                    // .tripId(...) ← don't include this
                    .riderId(tripDto.getRiderId())
                    .driverId(tripDto.getDriverId())
                    .pickupLocation(tripDto.getPickupLocation())
                    .dropoffLocation(tripDto.getDropoffLocation())
                    .startTime(tripDto.getStartTime())
                    .estimatedArrivalTime(tripDto.getEstimatedArrivalTime())
                    .status(tripDto.getStatus())
                    .build();


            tripRepo.save(trip);
            System.out.println("✅ Trip saved with auto-generated tripId:");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
