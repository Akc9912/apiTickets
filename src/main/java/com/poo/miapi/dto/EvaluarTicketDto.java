package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EvaluarTicketDto {
    @NotNull(message = "El idTrabajador no puede ser nulo")
    private int idTrabajador;

    @NotNull(message = "El idTicket no puede ser nulo")
    private int idTicket;

    @NotNull(message = "El estado de resolución no puede ser nulo")
    private boolean fueResuelto;

    @NotBlank(message = "El motivo de la falla es obligatorio")
    private String motivoFalla;

    // Getters y setters
    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
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