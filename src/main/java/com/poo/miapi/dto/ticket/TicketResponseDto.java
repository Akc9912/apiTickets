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

    @NotNull(message = "El estado del ticket es obligatorio")
    private EstadoTicket estado;

    private String creador; // Puede ser null si no hay creador
    private String tecnicoAsignado; // Puede ser null si no hay técnico

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

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(String tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}