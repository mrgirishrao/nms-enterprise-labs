package com.telecom.nms.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class TicketApplication {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }

    @GetMapping("/tickets/create")
    public Map<String, String> createTicket() {
        return Map.of(
            "ticketId", UUID.randomUUID().toString(),
            "status", "OPEN",
            "processedByPort", port
        );
    }
}