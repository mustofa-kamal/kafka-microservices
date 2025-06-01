// Step 5: Implement RefundEventProducer to send refund events
package com.example.tripservice.kafka.producer;

import com.example.tripservice.kafka.dto.RefundEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendRefundEvent(UUID tripId, String reason) {
        RefundEventDto dto = new RefundEventDto(tripId, reason);
        // convert to JSON manually or use configured ObjectMapper
        String json = "{\"tripId\":\"" + tripId + "\", \"reason\":\"" + reason + "\"}";
        kafkaTemplate.send("refund-events", tripId.toString(), json);
        System.out.println("♻️ Sent refund event for trip: " + tripId);
    }
}
