package com.example.ifood.repository;

import com.example.ifood.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    @Query(value = "SELECT * FROM RESTAURANTES r WHERE LOWER(r.CATEGORIA) = LOWER(:categoria)", nativeQuery = true)
    List<Restaurante> buscarPorCategoriaNativa(String categoria);
}
