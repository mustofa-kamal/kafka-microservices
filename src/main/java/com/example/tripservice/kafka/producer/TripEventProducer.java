package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TripEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public void sendTripEvent(TripDto tripDto) {
        try {
            String tripDtoJson = objectMapper.writeValueAsString(tripDto);
            kafkaTemplate.send("trip-events-v2", tripDto.getTripId().toString(), tripDtoJson);
            System.out.println("✅ Trip tripDto sent with tripId: " + tripDto.getTripId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
