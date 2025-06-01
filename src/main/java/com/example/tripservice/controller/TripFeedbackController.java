package com.example.tripservice.controller;

import com.example.tripservice.kafka.dto.TripFeedbackDto;
import com.example.tripservice.kafka.producer.TripFeedbackProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class TripFeedbackController {

    private final TripFeedbackProducer feedbackProducer;

    public TripFeedbackController(TripFeedbackProducer feedbackProducer) {
        this.feedbackProducer = feedbackProducer;
    }

    @PostMapping
    public ResponseEntity<String> submitFeedback(@RequestBody TripFeedbackDto dto) {
        feedbackProducer.sendFeedback(dto);
        return ResponseEntity.ok("Feedback sent");
    }
}
