package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.RiderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RiderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RiderEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRiderEvent(RiderDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("rider-events-v2", String.valueOf(event.getRiderId()), message);
            System.out.println("✅ Event sent with key: " + event.getRiderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

