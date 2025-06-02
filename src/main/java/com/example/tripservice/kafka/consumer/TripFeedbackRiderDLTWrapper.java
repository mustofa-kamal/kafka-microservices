package com.example.tripservice.kafka.consumer;

import com.example.tripservice.kafka.dto.TripFeedbackDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class TripFeedbackRiderDLTWrapper {

    private final TripFeedbackRider tripFeedbackRider;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripFeedbackRiderDLTWrapper(TripFeedbackRider tripFeedbackRider, ObjectMapper objectMapper,
                                       KafkaTemplate<String, String> kafkaTemplate ){

        this.kafkaTemplate = kafkaTemplate;
        this.tripFeedbackRider=tripFeedbackRider;
        this.objectMapper = objectMapper;

    }

    @KafkaListener(topics = "trip-feedback-rider-events", groupId = "trip-feedback-group")
    public void safeConsume(String message) {
        try {
            TripFeedbackDto dto = objectMapper.readValue(message, TripFeedbackDto.class);
            tripFeedbackRider.consumeFeedback(dto);
        } catch (Exception e) {
            System.err.println("❌ Failed to process feedback, sending to DLT: " + e.getMessage());
            kafkaTemplate.send("trip-feedback-dlt", message);
        }
    }
}
