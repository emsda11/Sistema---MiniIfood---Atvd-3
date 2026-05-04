package com.example.ifood.controller;

import com.example.ifood.dto.restaurante.RestauranteRequestDTO;
import com.example.ifood.dto.restaurante.RestauranteResponseDTO;
import com.example.ifood.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {
    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<RestauranteResponseDTO> criar(@Valid @RequestBody RestauranteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<List<RestauranteResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE')")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorCategoria(nome));
    }
}
