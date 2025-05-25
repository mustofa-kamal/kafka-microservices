package com.example.tripservice.repository;

import com.example.tripservice.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverEntityRepository extends JpaRepository<DriverEntity, Long> {
}
