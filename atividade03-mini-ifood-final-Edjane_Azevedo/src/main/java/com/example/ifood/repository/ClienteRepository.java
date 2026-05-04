package com.example.ifood.repository;

import com.example.ifood.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> buscarPorNomeParcial(String nome);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId")
    Integer contarPedidosPorCliente(Long clienteId);
}