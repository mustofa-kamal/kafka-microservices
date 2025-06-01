package com.example.tripservice.entity;

public enum Status {
    REQUESTED,
    DRIVER_ASSIGNED,
    STARTED,
    COMPLETED,             // Normal end of trip
    COMPLETED_DISPUTED,    // Rider says "No" to trip completion
    REFUNDED,              // Refund issued
    CANCELLED              // Cancelled at any stage before completion
}
