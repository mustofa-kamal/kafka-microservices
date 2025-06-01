package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripFeedbackDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripFeedbackProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripFeedbackProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendFeedback(TripFeedbackDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("trip-feedback-rider-events", dto.getTripId().toString(), json);
            System.out.println("📤 Sent feedback for trip " + dto.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
