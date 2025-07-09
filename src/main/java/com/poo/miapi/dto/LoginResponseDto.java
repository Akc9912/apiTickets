package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginResponseDto {
    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "El usuario es obligatorio")
    private UsuarioResponseDto usuario;

    public LoginResponseDto(String token, UsuarioResponseDto usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }
}
