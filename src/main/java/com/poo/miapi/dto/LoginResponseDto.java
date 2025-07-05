package com.poo.miapi.dto;

public class LoginResponseDto {
    private String token;
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
