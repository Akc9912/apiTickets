package com.poo.miapi.dto;

import java.time.LocalDateTime;

public class HistorialValidacionDto {
    private int idTicket;
    private boolean fueResuelto;
    private String comentario;
    private LocalDateTime fecha;

    public HistorialValidacionDto(int idTicket, boolean fueResuelto, String comentario, LocalDateTime fecha) {
        this.idTicket = idTicket;
        this.fueResuelto = fueResuelto;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public boolean isFueResuelto() {
        return fueResuelto;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}