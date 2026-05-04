package com.example.ifood.service;

import com.example.ifood.dto.restaurante.RestauranteRequestDTO;
import com.example.ifood.dto.restaurante.RestauranteResponseDTO;
import com.example.ifood.exception.BusinessException;
import com.example.ifood.exception.ResourceNotFoundException;
import com.example.ifood.model.Restaurante;
import com.example.ifood.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RestauranteService {
    private final RestauranteRepository repository;

    public RestauranteService(RestauranteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RestauranteResponseDTO criar(RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        return toDTO(repository.save(restaurante));
    }

    public List<RestauranteResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public RestauranteResponseDTO buscarPorId(Long id) {
        return toDTO(obterEntidade(id));
    }

    @Transactional
    public RestauranteResponseDTO atualizar(Long id, RestauranteRequestDTO dto) {
        Restaurante restaurante = obterEntidade(id);
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        return toDTO(repository.save(restaurante));
    }

    @Transactional
    public void deletar(Long id) {
        Restaurante restaurante = obterEntidade(id);
        if (!restaurante.getProdutos().isEmpty()) {
            throw new BusinessException("Não é possível remover restaurante com produtos vinculados.");
        }
        repository.delete(restaurante);
    }

    public List<RestauranteResponseDTO> buscarPorCategoria(String categoria) {
        return repository.buscarPorCategoriaNativa(categoria).stream().map(this::toDTO).toList();
    }

    public Restaurante obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante com id " + id + " não encontrado."));
    }

    private RestauranteResponseDTO toDTO(Restaurante restaurante) {
    return new RestauranteResponseDTO(
            restaurante.getId(),
            restaurante.getNome(),
            restaurante.getCategoria(),
            0
    );
}
}
