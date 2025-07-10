package com.poo.miapi.dto.ticket;

import java.time.LocalDateTime;

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

    @NotBlank(message = "El creador del ticket es obligatorio")
    private String creador;

    @NotBlank(message = "El técnico asignado es obligatorio")
    private String tecnicoAsignado;

    @NotNull(message = "La fecha de creación no puede ser nula")
    private LocalDateTime fechaCreacion;

    @NotNull(message = "La fecha de última actualización no puede ser nula")
    private LocalDateTime fechaUltimaActualizacion;

    // Constructor
    public TicketResponseDto() {
    }

    public TicketResponseDto(int id, String titulo, String descripcion, EstadoTicket estado, String creador,
            String tecnicoAsignado, LocalDateTime fechaCreacion, LocalDateTime fechaUltimaActualizacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.creador = creador;
        this.tecnicoAsignado = tecnicoAsignado;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
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

    public String getCreador() {
        return creador;
    }

    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }
}