package com.telecom.nms.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private static final String TOPIC = "telecom-network-alarms";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/dispatch")
    public Mono<ResponseEntity<Map<String, String>>> dispatchAlarm(@RequestBody Map<String, Object> alarmPayload) {
        return Mono.fromRunnable(() -> {
            // Extracting standard network elements to use as the routing Partition Key
            String baseStationId = (String) alarmPayload.getOrDefault("equipmentId", "GENERIC_NODE");
            
            // Non-blocking fire-and-forget push to Kafka broker commit log
            kafkaTemplate.send(TOPIC, baseStationId, alarmPayload);
        }).thenReturn(ResponseEntity.ok(Map.of(
            "status", "QUEUED",
            "message", "Alarm telemetry event accepted by streaming broker shadow buffer."
        )));
    }
}