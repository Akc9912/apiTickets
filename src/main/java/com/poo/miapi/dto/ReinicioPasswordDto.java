package com.poo.miapi.dto;

import jakarta.validation.constraints.NotNull;

public class ReinicioPasswordDto {
    @NotNull(message = "El idUsuario es obligatorio")
    private int idUsuario;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}