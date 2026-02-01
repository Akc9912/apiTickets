package com.poo.miapi.module.auth.dto;



import com.poo.miapi.module.user.dto.UserResponseDto;

import jakarta.validation.constraints.NotBlank;

public class LoginResponseDto {
    @NotBlank(message = "El token es obligatorio")
    private String token;

    private UserResponseDto usuario;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, UserResponseDto usuario) {
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

    public UserResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UserResponseDto usuario) {
        this.usuario = usuario;
    }
}
