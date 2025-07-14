package com.poo.miapi.dto.notificacion;

import java.time.LocalDateTime;

public class NotificacionResponseDto {
    private int id;
    private int idUsuario;
    private String mensaje;
    private LocalDateTime fechaCreacion;

    public NotificacionResponseDto() {
    }

    public NotificacionResponseDto(int id, int idUsuario, String mensaje, LocalDateTime fechaCreacion) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
