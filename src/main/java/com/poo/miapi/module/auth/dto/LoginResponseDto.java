package com.poo.miapi.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.poo.miapi.module.user.dto.UserResponseDto;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login response with JWT token and user information")
public class LoginResponseDto {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    @NotBlank(message = "El token es obligatorio")
    private String token;

    @Schema(description = "Authenticated user information")
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
