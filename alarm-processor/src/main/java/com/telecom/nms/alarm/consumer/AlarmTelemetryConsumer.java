package com.telecom.nms.alarm.consumer;

import com.telecom.nms.shared.event.TicketEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.telecom.nms.alarm.model.AlarmTelemetryAvro; // 🚀 Swap Map out for your Avro class!
import java.util.UUID;

@Service
public class AlarmTelemetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlarmTelemetryConsumer.class);

    // 🚀 INJECT KAFKA TEMPLATE: Allows the consumer to also act as a producer
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TICKET_TOPIC = "telecom-incident-tickets";

public AlarmTelemetryConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "telecom-network-alarms", groupId = "nms-alarm-processing-group-v1")
    public void consumeAlarmStream(
            @Payload AlarmTelemetryAvro message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        String equipmentId =  message.get("equipmentId").toString();
        String severity =  message.get("severity").toString();
        String problem =  message.get("problem").toString();

        log.info("==================================================================");
        log.info("🚀 KAFKA STREAM INGESTED FROM PARTITION: {} AT OFFSET: {}", partition, offset);
        log.info("🔑 ROUTING KEY [Equipment Identity]: {}", equipmentId);
        log.info("📋 SEVERITY: {} | SPECIFIC PROBLEM: {}", severity, problem);
        log.info("==================================================================");

        // 🧠 AUTOMATION BUSINESS RULE EVALUATION
        if ("CRITICAL".equalsIgnoreCase(severity)) {
            log.info("⚠️ ALERT DETECTED AS CRITICAL! Initiating ticket auto-generation...");
        }
        if ("CRASH".equalsIgnoreCase(severity)) {
        throw new IllegalArgumentException("UNHANDLED TELECOM METRIC FAULT: Severity 'CRASH' is an illegal monitoring state!");
    }    
    
            // Generate ticket entity details
            String shortTicketId = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String workOrderSummary = "AUTOMATED CRITICAL FAULT REPAIR REQUIRED: " + problem;
            
            TicketEvent ticketEvent = new TicketEvent(
                shortTicketId,
                equipmentId,
                workOrderSummary,
                "OPEN"
            );

            // 📤 FIRE DOWNSTREAM EVENT: Forward matching key to retain network element ordering
            kafkaTemplate.send(TICKET_TOPIC, equipmentId, ticketEvent);
            log.info("🎯 TicketEvent [{}] published to Kafka topic '{}'", shortTicketId, TICKET_TOPIC);
        }
    }