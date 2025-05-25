package com.example.tripservice.repository;

import com.example.tripservice.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripEntityRepository extends JpaRepository<TripEntity, Long> {}
