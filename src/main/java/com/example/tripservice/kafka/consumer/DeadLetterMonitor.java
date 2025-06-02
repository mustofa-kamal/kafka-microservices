package com.example.tripservice.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterMonitor {

    @KafkaListener(topics = "trip-feedback-dlt", groupId = "dlt-monitor")
    public void handleDLT(String message) {
        System.out.println("📦 Message in DLT: " + message);
    }
}
