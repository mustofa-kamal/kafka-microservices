package com.example.tripservice.repository;

import com.example.tripservice.entity.TripViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;



public interface TripViewRepository extends JpaRepository<TripViewEntity, UUID> { }
