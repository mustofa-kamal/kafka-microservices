package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.TripDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();



    public TripEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    public void sendTripEvent(TripDto event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("trip-events-v2", String.valueOf(event.getRiderId()), json);

            System.out.println("✅ Trip event sent for tripId: " );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
