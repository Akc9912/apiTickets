package com.poo.miapi.dto;

import java.time.LocalDateTime;

public class IncidenteTecnicoDto {
    private int tecnicoId;
    private int ticketId;
    private String tipo;
    private String motivo;
    private LocalDateTime fecha;

    public IncidenteTecnicoDto(int tecnicoId, int ticketId, String tipo, String motivo, LocalDateTime fecha) {
        this.tecnicoId = tecnicoId;
        this.ticketId = ticketId;
        this.tipo = tipo;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public int getTecnicoId() {
        return tecnicoId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}