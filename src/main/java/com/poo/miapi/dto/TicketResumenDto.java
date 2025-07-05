package com.poo.miapi.dto;

public class TicketResumenDto {
    private int id;
    private String titulo;
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
