package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DriverAssignedProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DriverAssignedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendDriverAssignedEvent(TripDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("trip-events-v2", dto.getTripId().toString(), json);
            System.out.println("📤 Sent DRIVER_ASSIGNED event for tripId: " + dto.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
