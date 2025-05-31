package com.example.tripservice.kafka.consumer;

import com.example.tripservice.entity.Status;
import com.example.tripservice.entity.TripEntity;
import com.example.tripservice.kafka.dto.TripCompensationDto;
import com.example.tripservice.repository.TripEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripCompensationConsumer {

    private final TripEntityRepository tripRepo;

    @KafkaListener(topics = "trip-events-compensate", groupId = "trip-saga")
    public void handleCompensation(TripCompensationDto dto) {
        Optional<TripEntity> optional = tripRepo.findById(dto.getTripId());
        if (optional.isPresent()) {
            TripEntity entity = optional.get();
            entity.setTripStatus(Status.valueOf(dto.getRevertToStatus().toString()));
            tripRepo.save(entity);
            System.out.println("↩️ Compensation applied: trip " + dto.getTripId() + " reverted to " + dto.getRevertToStatus());
        } else {
            System.out.println("❌ Trip not found for compensation: " + dto.getTripId());
        }
    }
}
