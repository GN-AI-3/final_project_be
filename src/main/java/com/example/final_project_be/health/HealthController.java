package com.example.final_project_be.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// 서버가 정상적으로 작동하고 있는지 확인하기 위해 사용
@RestController
@RequestMapping("/health")
public class HealthController {

    private final String uniqueId = UUID.randomUUID().toString();

    @GetMapping
    public String checkHealth() {return "Server ID" + uniqueId;}
}
