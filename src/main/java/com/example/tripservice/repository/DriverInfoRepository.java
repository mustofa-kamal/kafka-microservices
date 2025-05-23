package com.example.tripservice.repository;

import com.example.tripservice.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverInfoRepository extends JpaRepository<DriverEntity, Long> {
}
