package com.telecom.nms.alarm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Map;

// ARCHITECT NOTE: Notice we do NOT provide a URL string here. 
// We simply point value to the exact service name registered in Eureka!
@FeignClient(value = "ticket-service")
public interface TicketClient {

    @GetMapping("/tickets/create")
    @CircuitBreaker(name = "ticketServiceCB", fallbackMethod = "handleTicketCreationFailure")
    Map<String, String> triggerTicketCreation();
    // 2. THE LOCAL FALLBACK METHOD
    // Why: If the real ticket-service throws an error, this runs locally.
    // Rule: Must return Map<String, String> and accept a Throwable error input.
    default Map<String, String> handleTicketCreationFailure(Throwable error) {
        return Map.of(
            "ticketId", "00000000-0000-0000-0000-000000000000",
            "status", "FALLBACK_CACHED_LOCALLY",
            "errorContext", error.getMessage()
        );
    }
}