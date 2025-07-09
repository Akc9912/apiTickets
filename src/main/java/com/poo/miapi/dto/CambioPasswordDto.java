package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CambioPasswordDto {
    @NotNull(message = "El idUsuario es obligatorio")
    private Integer idUsuario;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;

    // Getters y setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}
