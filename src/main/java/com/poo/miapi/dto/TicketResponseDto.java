package com.poo.miapi.dto;

import com.poo.miapi.model.core.EstadoTicket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketResponseDto {
    @NotNull(message = "El id del ticket no puede ser nulo")
    private int id;

    @NotBlank(message = "El título del ticket es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción del ticket es obligatoria")
    private String descripcion;

    @NotBlank(message = "El estado del ticket es obligatorio")
    private EstadoTicket estado;

    @NotBlank(message = "El técnico asignado es obligatorio")
    private String tecnicoAsignado;

    public TicketResponseDto(int id, String titulo, String descripcion, EstadoTicket estado, String tecnicoAsignado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.tecnicoAsignado = tecnicoAsignado;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }
}