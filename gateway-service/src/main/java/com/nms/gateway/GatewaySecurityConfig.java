package com.telecom.nms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            // 1. Disable CSRF because our API is stateless and driven purely by JWT tokens
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            // 2. Define the traffic routing permission rules
            .authorizeExchange(exchanges -> exchanges
                // Public Routes: Let discovery dashboards and configurations pass without tokens
                .pathMatchers("/eureka/**").permitAll()
                .pathMatchers("/api/v1/telemetry/dispatch").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                
                // Secured Routes: Every single NMS service endpoint requires an authenticated JWT passport
                .anyExchange().authenticated()
            )
            
            // 3. Configure the Gateway to operate as an OAuth2 Resource Server using JWT verification
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtSpec -> {}) // Spring implicitly uses our issuer-uri configuration to build a ReactiveJwtDecoder
            )
            .build();
    }
}