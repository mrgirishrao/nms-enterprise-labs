package com.telecom.nms.ticket.consumer;

import com.telecom.nms.shared.event.TicketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TicketEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketEventConsumer.class);

    @KafkaListener(topics = "telecom-incident-tickets", groupId = "nms-ticket-generation-group")
    public void handleTicketCreation(@Payload TicketEvent event) {
        log.info("==================================================================");
        log.info("🎫 NEW INCIDENT TICKET GENERATED AUTOMATICALLY VIA KAFKA Pipeline");
        log.info("🆔 TICKET ID: {}", event.getTicketId());
        log.info("📡 AFFECTED ELEMENT: {}", event.getEquipmentId());
        log.info("📝 WORK ORDER SUMMARY: {}", event.getIssueDescription());
        log.info("📊 CURRENT STATUS: {}", event.getStatus());
        log.info("==================================================================");
        
        // Down the line, this is where we will inject ticketRepository.save(new Ticket(...));
    }
}