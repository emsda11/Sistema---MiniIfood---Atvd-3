package com.example.ifood.service;

import com.example.ifood.dto.pedido.PedidoRequestDTO;
import com.example.ifood.dto.pedido.PedidoResponseDTO;
import com.example.ifood.exception.BusinessException;
import com.example.ifood.exception.ResourceNotFoundException;
import com.example.ifood.model.Cliente;
import com.example.ifood.model.Pedido;
import com.example.ifood.model.Produto;
import com.example.ifood.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository repository, ClienteService clienteService, ProdutoService produtoService) {
        this.repository = repository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteService.obterEntidade(dto.getClienteId());

        List<Produto> produtos = dto.getProdutosIds()
                .stream()
                .map(produtoService::obterEntidade)
                .toList();

        validarMesmoRestaurante(produtos);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(dto.getStatus());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setProdutos(produtos);
        pedido.setValorTotal(calcularValorTotal(produtos));

        Pedido salvo = repository.save(pedido);

        return toDTO(salvo);
    }

    public List<PedidoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido com id " + id + " não encontrado."));

        return toDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido com id " + id + " não encontrado."));

        Cliente cliente = clienteService.obterEntidade(dto.getClienteId());

        List<Produto> produtos = dto.getProdutosIds()
                .stream()
                .map(produtoService::obterEntidade)
                .toList();

        validarMesmoRestaurante(produtos);

        pedido.setCliente(cliente);
        pedido.setStatus(dto.getStatus());
        pedido.setProdutos(produtos);
        pedido.setValorTotal(calcularValorTotal(produtos));

        Pedido salvo = repository.save(pedido);

        return toDTO(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido com id " + id + " não encontrado."));

        repository.delete(pedido);
    }

    public List<PedidoResponseDTO> buscarPorStatus(String status) {
        return repository.buscarPorStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private BigDecimal calcularValorTotal(List<Produto> produtos) {
        return produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarMesmoRestaurante(List<Produto> produtos) {
        Long primeiroRestauranteId = null;

        for (Produto produto : produtos) {
            Long restauranteId = produto.getRestaurante() != null
                    ? produto.getRestaurante().getId()
                    : null;

            if (primeiroRestauranteId == null) {
                primeiroRestauranteId = restauranteId;
            } else if (!primeiroRestauranteId.equals(restauranteId)) {
                throw new BusinessException("Um pedido deve conter produtos do mesmo restaurante.");
            }
        }
    }

   private PedidoResponseDTO toDTO(Pedido pedido) {
    Long clienteId = null;
    String clienteNome = "";

    if (pedido.getCliente() != null) {
        clienteId = pedido.getCliente().getId();
        clienteNome = clienteService.obterEntidade(clienteId).getNome();
    }

    return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getStatus(),
            pedido.getDataCriacao(),
            pedido.getValorTotal(),
            clienteId,
            clienteNome,
            List.of()
    );
}
}