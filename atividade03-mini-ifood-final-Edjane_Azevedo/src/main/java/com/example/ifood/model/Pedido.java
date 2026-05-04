package com.example.ifood.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String status;

    @Column(nullable=false)
    private LocalDateTime dataCriacao;

    @Column(nullable=false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable=false)
    private Cliente cliente;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "pedido_produto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private List<Produto> produtos = new ArrayList<>();

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public Cliente getCliente() { return cliente; }
    public List<Produto> getProdutos() { return produtos; }

    public void setId(Long id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
}
