package com.poo.miapi.dto.historial;

import jakarta.validation.constraints.NotNull;

public class HistorialValidacionRequestDto {
    @NotNull(message = "El ID del usuario validador es obligatorio")
    private int idUsuarioValidador;

    @NotNull(message = "El ID del ticket es obligatorio")
    private int idTicket;

    @NotNull(message = "Debe indicar si fue aprobado")
    private boolean fueAprobado;

    private String comentario; // Opcional

    public HistorialValidacionRequestDto() {
    }

    public HistorialValidacionRequestDto(int idUsuarioValidador, int idTicket, boolean fueAprobado, String comentario) {
        this.idUsuarioValidador = idUsuarioValidador;
        this.idTicket = idTicket;
        this.fueAprobado = fueAprobado;
        this.comentario = comentario;
    }

    // Getters y setters
    public int getIdUsuarioValidador() {
        return idUsuarioValidador;
    }

    public void setIdUsuarioValidador(int idUsuarioValidador) {
        this.idUsuarioValidador = idUsuarioValidador;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public boolean isFueAprobado() {
        return fueAprobado;
    }

    public void setFueAprobado(boolean fueAprobado) {
        this.fueAprobado = fueAprobado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}