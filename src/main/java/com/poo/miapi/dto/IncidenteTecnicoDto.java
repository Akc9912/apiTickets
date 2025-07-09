package com.poo.miapi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncidenteTecnicoDto {
    @NotNull(message = "El idTecnico no puede ser nulo")
    private int tecnicoId;

    @NotNull(message = "El idTicket no puede ser nulo")
    private int ticketId;

    @NotBlank(message = "El tipo de incidente es obligatorio")
    private String tipo;

    @NotBlank(message = "El motivo del incidente es obligatorio")
    private String motivo;

    @NotNull(message = "La fecha del incidente no puede ser nula")
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