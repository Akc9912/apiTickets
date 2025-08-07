package com.poo.miapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChangePasswordDto {

    @NotNull(message = "El ID del usuario no puede ser nulo")
    private int userId;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(int userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
