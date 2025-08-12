package com.poo.miapi.dto.auth;

import com.poo.miapi.dto.usuarios.UsuarioResponseDto;

import jakarta.validation.constraints.NotBlank;

public class LoginResponseDto {
    @NotBlank(message = "El token es obligatorio")
    private String token;

    private UsuarioResponseDto usuario;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, UsuarioResponseDto usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDto usuario) {
        this.usuario = usuario;
    }
}
