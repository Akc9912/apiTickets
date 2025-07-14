package com.poo.miapi.dto.ticket;

import jakarta.validation.constraints.NotNull;

public class EvaluarTicketDto {

    @NotNull(message = "El ID del trabajador es obligatorio")
    private int idTrabajador;

    @NotNull(message = "Debe indicar si fue resuelto")
    private boolean fueResuelto;

    private String motivoFalla; // Opcional, solo si no fue resuelto

    public EvaluarTicketDto() {
    }

    public EvaluarTicketDto(int idTrabajador, boolean fueResuelto, String motivoFalla) {
        this.idTrabajador = idTrabajador;
        this.fueResuelto = fueResuelto;
        this.motivoFalla = motivoFalla;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
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
