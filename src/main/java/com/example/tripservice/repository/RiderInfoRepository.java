package com.example.tripservice.repository;

import com.example.tripservice.entity.RiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInfoRepository extends JpaRepository<RiderEntity, Long> {
}
