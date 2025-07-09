package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketResumenDto {
    @NotNull(message = "El id del ticket no puede ser nulo")
    private int id;

    @NotBlank(message = "El título del ticket es obligatorio")
    private String titulo;

    @NotBlank(message = "El estado del ticket es obligatorio")
    private String estado;

    public TicketResumenDto(int id, String titulo, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEstado() {
        return estado;
    }
}
