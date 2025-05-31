package com.example.tripservice.repository;

import com.example.tripservice.entity.TripStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripStatusHistoryRepository extends JpaRepository<TripStatusHistoryEntity, UUID> {
    List<TripStatusHistoryEntity> findByTripId(UUID tripId);

    List<TripStatusHistoryEntity> findByTripIdOrderByStatusUpdatedAtAsc(UUID tripId);

}