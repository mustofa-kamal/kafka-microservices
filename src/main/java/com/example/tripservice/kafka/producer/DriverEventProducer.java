package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.DriverDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DriverEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DriverEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDriverEvent(DriverDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("driver-events-v2", String.valueOf(event.getDriverId()), message);
            System.out.println("✅ Driver event sent for driverId: " + event.getDriverId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
