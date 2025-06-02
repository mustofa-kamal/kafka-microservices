// Step 3: Create a Kafka consumer for trip-feedback-events
package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.entity.TripStatusHistoryEntity;
import com.example.tripservice.kafka.dto.TripDto;
import com.example.tripservice.kafka.dto.TripFeedbackDto;
import com.example.tripservice.kafka.producer.RefundEventProducer;
import com.example.tripservice.repository.TripEntityRepository;
import com.example.tripservice.repository.TripStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service

public class TripFeedbackRider {

    private final TripEntityRepository tripRepo;
    private final RefundEventProducer refundProducer;
    private final ObjectMapper objectMapper;
    private final TripStatusHistoryRepository historyRepo;

    public TripFeedbackRider(TripEntityRepository tripRepo, RefundEventProducer refundProducer,
                             ObjectMapper objectMapper, TripStatusHistoryRepository historyRepo) {
        this.refundProducer = refundProducer;
        this.tripRepo = tripRepo;
        this.objectMapper = objectMapper;
        this.historyRepo = historyRepo;
    }


    //@KafkaListener(topics = "trip-feedback-rider-events", groupId = "trip-feedback-group")
    public void consumeFeedback(TripFeedbackDto feedback) {
        try {
            //TripFeedbackDto feedback = objectMapper.readValue(message, TripFeedbackDto.class);
            Optional<TripEntity> optional = tripRepo.findById(feedback.getTripId());
            if (optional.isEmpty()) {
                System.out.println("❌ Feedback received for unknown trip: " + feedback.getTripId());
                return;
            }

            TripEntity trip = optional.get();

            if (feedback.isConfirmed()) {
                System.out.println("✅ Rider confirmed trip completion: " + feedback.getTripId());
                // No state change, trip stays COMPLETED
            } else {
                if (trip.getTripStatus() != Status.COMPLETED) {
                    System.out.println("⚠️ Trip is not in COMPLETED state. Duplicate or invalid dispute: " + feedback.getTripId());
                    return;
                }

                System.out.println("🚫 Rider disputed trip: " + feedback.getTripId());
                trip.setTripStatus(Status.COMPLETED_DISPUTED);
                tripRepo.save(trip);

                TripStatusHistoryEntity history = TripStatusHistoryEntity.builder()
                        .tripId(trip.getTripId())
                        .riderId(trip.getRiderId())
                        .driverId(trip.getDriverId())
                        .pickupLocation(trip.getPickupLocation())
                        .dropoffLocation(trip.getDropoffLocation())
                        .requestedPickupTime(trip.getRequestedPickupTime())
                        .driverAssignedTime(trip.getDriverAssignedTime())
                        .actualPickupTime(trip.getActualPickupTime())
                        .estimatedDropoffTime(trip.getEstimatedDropoffTime())
                        .actualDropoffTime(trip.getActualDropoffTime())
                        .tripStatus(Status.COMPLETED_DISPUTED)
                        .statusUpdatedAt(LocalDateTime.now())
                        .build();

                historyRepo.save(history);

                // trigger refund compensation
                refundProducer.sendRefundEvent(feedback.getTripId(), feedback.getReason());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}