package com.example.ifood.service;

import com.example.ifood.dto.produto.ProdutoRequestDTO;
import com.example.ifood.dto.produto.ProdutoResponseDTO;
import com.example.ifood.exception.ResourceNotFoundException;
import com.example.ifood.model.Produto;
import com.example.ifood.model.Restaurante;
import com.example.ifood.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;
    private final RestauranteService restauranteService;

    public ProdutoService(ProdutoRepository repository, RestauranteService restauranteService) {
        this.repository = repository;
        this.restauranteService = restauranteService;
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Restaurante restaurante = restauranteService.obterEntidade(dto.getRestauranteId());
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setRestaurante(restaurante);
        return toDTO(repository.save(produto));
    }

    public List<ProdutoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        return toDTO(obterEntidade(id));
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = obterEntidade(id);
        Restaurante restaurante = restauranteService.obterEntidade(dto.getRestauranteId());
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setRestaurante(restaurante);
        return toDTO(repository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(obterEntidade(id));
    }

    public List<ProdutoResponseDTO> buscarPorRestauranteComJoinFetch(Long restauranteId) {
        return repository.buscarProdutosComRestaurante(restauranteId).stream().map(this::toDTO).toList();
    }

    public List<ProdutoResponseDTO> buscarPorPrecoMinimo(BigDecimal precoMinimo) {
        return repository.buscarPorPrecoMinimo(precoMinimo).stream().map(this::toDTO).toList();
    }

    public Produto obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto com id " + id + " não encontrado."));
    }

    private ProdutoResponseDTO toDTO(Produto produto) {
    Long restauranteId = null;
    String restauranteNome = "";

    if (produto.getRestaurante() != null) {
        restauranteId = produto.getRestaurante().getId();
    }

    return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getPreco(),
            restauranteId,
            restauranteNome
    );
}
}
