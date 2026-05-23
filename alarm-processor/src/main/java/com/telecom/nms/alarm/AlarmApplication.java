package com.telecom.nms.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@EnableFeignClients // <--- CRITICAL: This activates the Feign compilation scan!
@RestController
public class AlarmApplication {

    @Autowired
    private TicketClient ticketClient;

    public static void main(String[] args) {
        SpringApplication.run(AlarmApplication.class, args);
    }

    @GetMapping("/dispatch-alarm")
    public Map<String, Object> dispatchNetworkAlarm() {
        // 1. Simulate finding a network fault
        Map<String, Object> response = new HashMap<>();
        response.put("alarmStatus", "CRITICAL");
        response.put("equipmentId", "ROUTER-UDUPI-04");

        // 2. Cross-service invocation via client-side load balancer
        Map<String, String> ticketDetails = ticketClient.triggerTicketCreation();
        response.put("associatedTicket", ticketDetails);

        return response;
    }
}