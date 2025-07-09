package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificacionRequestDto {
    @NotNull(message = "El id del usuario no puede ser nulo")
    private int idUsuario;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    // Getters y setters
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
}
