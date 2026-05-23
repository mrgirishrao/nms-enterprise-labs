package com.telecom.nms.alarm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

// ARCHITECT NOTE: Notice we do NOT provide a URL string here. 
// We simply point value to the exact service name registered in Eureka!
@FeignClient(value = "ticket-service")
public interface TicketClient {

    @GetMapping("/tickets/create")
    Map<String, String> triggerTicketCreation();
}