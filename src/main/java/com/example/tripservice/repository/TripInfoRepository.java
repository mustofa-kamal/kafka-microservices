package com.example.tripservice.repository;

import com.example.tripservice.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripInfoRepository extends JpaRepository<TripEntity, Long> {}
