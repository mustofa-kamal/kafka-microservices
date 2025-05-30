package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTripEvent(TripDto event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("trip-events-v2", event.getTripId().toString(), json);
            System.out.println("✅ Trip event sent: " + event.getTripStatus() + " for tripId: " + event.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
