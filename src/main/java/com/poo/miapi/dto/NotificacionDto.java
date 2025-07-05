package com.poo.miapi.dto;

import java.time.LocalDateTime;

public class NotificacionDto {
    private Long id;
    private String mensaje;
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