package com.poo.miapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChangePasswordDto {

    /**
     * DTO para cambiar la contraseña de un usuario.
     * Contiene el campo de contraseña nueva y el ID del usuario.
     * Este DTO se utiliza cuando un usuario desea cambiar su contraseña,
     */

    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long userId;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(Long id, String newPassword) {
        this.userId = id;
        this.newPassword = newPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
