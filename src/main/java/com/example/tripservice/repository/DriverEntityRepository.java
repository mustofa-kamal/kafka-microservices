package com.example.tripservice.repository;

import com.example.tripservice.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverEntityRepository extends JpaRepository<DriverEntity, UUID> {
}
