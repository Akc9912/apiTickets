package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FiltroTicketsDto {
    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotNull(message = "El ID del creador es obligatorio")
    private int creadorId;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Integer creadorId) {
        this.creadorId = creadorId;
    }
}
