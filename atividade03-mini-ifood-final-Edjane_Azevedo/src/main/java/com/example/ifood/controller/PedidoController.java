package com.example.ifood.controller;

import com.example.ifood.dto.pedido.PedidoRequestDTO;
import com.example.ifood.dto.pedido.PedidoResponseDTO;
import com.example.ifood.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE')")
    public ResponseEntity<PedidoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MASTER','GERENTE','AUDITOR')")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorStatus(@RequestParam String valor) {
        return ResponseEntity.ok(service.buscarPorStatus(valor));
    }
}
