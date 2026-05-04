package com.example.ifood.dto.restaurante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestauranteRequestDTO {
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "A categoria é obrigatória.")
    private String categoria;

    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
