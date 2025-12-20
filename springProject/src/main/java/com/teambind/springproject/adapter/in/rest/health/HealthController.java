package com.teambind.springproject.adapter.in.rest.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(HealthResponse.up());
    }

    public record HealthResponse(String status) {
        public static HealthResponse up() {
            return new HealthResponse("UP");
        }
    }
}