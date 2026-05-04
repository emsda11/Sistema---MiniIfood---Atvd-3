package com.example.ifood.service;

import com.example.ifood.dto.cliente.ClienteRequestDTO;
import com.example.ifood.dto.cliente.ClienteResponseDTO;
import com.example.ifood.exception.BusinessException;
import com.example.ifood.exception.ResourceNotFoundException;
import com.example.ifood.model.Cliente;
import com.example.ifood.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        return toDTO(repository.save(cliente));
    }

    public List<ClienteResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        return toDTO(obterEntidade(id));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = obterEntidade(id);
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        return toDTO(repository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = obterEntidade(id);

        // ⚠️ Aqui também pode dar lazy se acessar direto
        if (cliente.getPedidos() != null && !cliente.getPedidos().isEmpty()) {
            throw new BusinessException("Não é possível remover cliente com pedidos vinculados.");
        }

        repository.delete(cliente);
    }

    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        return repository.buscarPorNomeParcial(nome).stream().map(this::toDTO).toList();
    }

    public Cliente obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com id " + id + " não encontrado."));
    }

   private ClienteResponseDTO toDTO(Cliente cliente) {
    Integer quantidadePedidos = repository.contarPedidosPorCliente(cliente.getId());

    return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            quantidadePedidos
    );
}
}