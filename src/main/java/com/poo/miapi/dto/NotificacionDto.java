package com.poo.miapi.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificacionDto {
    @NotNull(message = "El id de la notificación no puede ser nulo")
    private Long id;

    @NotBlank(message = "El mensaje de la notificación es obligatorio")
    private String mensaje;

    @NotBlank(message = "La fecha de la notificación es obligatoria")
    private LocalDateTime fecha;

    public NotificacionDto(Long id, String mensaje, LocalDateTime fecha) {
        this.id = id;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}