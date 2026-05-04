package com.example.ifood.dto.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {
    private Long id;
    private String status;
    private LocalDateTime dataCriacao;
    private BigDecimal valorTotal;
    private Long clienteId;
    private String clienteNome;
    private List<String> produtos;

    public PedidoResponseDTO(Long id, String status, LocalDateTime dataCriacao, BigDecimal valorTotal,
                             Long clienteId, String clienteNome, List<String> produtos) {
        this.id = id;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.valorTotal = valorTotal;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.produtos = produtos;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public Long getClienteId() { return clienteId; }
    public String getClienteNome() { return clienteNome; }
    public List<String> getProdutos() { return produtos; }
}
