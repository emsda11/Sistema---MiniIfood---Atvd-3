package com.example.ifood.repository;

import com.example.ifood.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH p.produtos WHERE p.id = :id")
    Pedido buscarPedidoComProdutos(Long id);

    @Query("SELECT p FROM Pedido p WHERE LOWER(p.status) = LOWER(:status)")
    List<Pedido> buscarPorStatus(String status);
}
