package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripRequestedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripRequestedProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripRequestedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendTripRequested(TripRequestedDto event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("trip-requested-events", event.getTripId().toString(), json);
            System.out.println("📤 TripRequestedEvent sent: " + event.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
