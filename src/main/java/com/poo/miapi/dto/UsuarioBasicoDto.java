package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioBasicoDto {
    @NotNull(message = "El id del usuario no puede ser nulo")
    private int id;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombre;

    public UsuarioBasicoDto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}