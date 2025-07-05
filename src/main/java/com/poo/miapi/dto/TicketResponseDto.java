package com.poo.miapi.dto;

import com.poo.miapi.model.core.EstadoTicket;

public class TicketResponseDto {
    private int id;
    private String titulo;
    private String descripcion;
    private EstadoTicket estado;
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