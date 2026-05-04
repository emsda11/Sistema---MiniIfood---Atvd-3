package com.example.ifood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class SistemaController {
    @GetMapping("/sistema/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "sistema", "Mini iFood API",
                "descricao", "API REST com Spring Security, DTOs, JPA e três roles.",
                "roles", new String[]{"ROLE_MASTER", "ROLE_GERENTE", "ROLE_AUDITOR"}
        ));
    }
}
