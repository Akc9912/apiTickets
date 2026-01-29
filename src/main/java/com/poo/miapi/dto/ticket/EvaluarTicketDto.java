package com.poo.miapi.dto.ticket;

import jakarta.validation.constraints.NotNull;

public class EvaluarTicketDto {

    @NotNull(message = "El ID del usuario validador es obligatorio")
    private int idUsuarioValidador;

    @NotNull(message = "Debe indicar si fue resuelto")
    private boolean fueResuelto;

    private String motivoFalla; // Opcional, solo si no fue resuelto

    public EvaluarTicketDto() {
    }

    public EvaluarTicketDto(int idUsuarioValidador, boolean fueResuelto, String motivoFalla) {
        this.idUsuarioValidador = idUsuarioValidador;
        this.fueResuelto = fueResuelto;
        this.motivoFalla = motivoFalla;
    }

    public int getIdUsuarioValidador() {
        return idUsuarioValidador;
    }

    public void setIdUsuarioValidador(int idUsuarioValidador) {
        this.idUsuarioValidador = idUsuarioValidador;
    }

    public boolean isFueResuelto() {
        return fueResuelto;
    }

    public void setFueResuelto(boolean fueResuelto) {
        this.fueResuelto = fueResuelto;
    }

    public String getMotivoFalla() {
        return motivoFalla;
    }

    public void setMotivoFalla(String motivoFalla) {
        this.motivoFalla = motivoFalla;
    }
}
