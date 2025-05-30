package com.example.tripservice.repository;

import com.example.tripservice.entity.RiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiderEntityRepository extends JpaRepository<RiderEntity, UUID> {
}
