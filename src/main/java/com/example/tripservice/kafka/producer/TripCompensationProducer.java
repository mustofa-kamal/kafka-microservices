package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripCompensationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripCompensationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripCompensationProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendCompensation(UUID tripId, Enum<?> rollbackTo, String reason) {
        try {
            TripCompensationDto dto = new TripCompensationDto(tripId, rollbackTo.name(), reason);
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("trip-events-compensate", tripId.toString(), json);
            System.out.println("🛠️ Compensation event sent: " + dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
