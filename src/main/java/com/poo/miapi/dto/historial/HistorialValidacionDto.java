package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HistorialValidacionDto {
    @NotNull(message = "El idTicket no puede ser nulo")
    private int idTicket;

    @NotNull(message = "El estado de resolución no puede ser nulo")
    private boolean fueResuelto;

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;

    @NotNull(message = "La fecha no puede ser nula")
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