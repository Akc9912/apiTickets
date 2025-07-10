package com.poo.miapi.dto.auth;

public class ResetPasswordDto {
    /**
     * DTO para el restablecimiento de contraseña a su valor por defecto.
     * Este DTO se utiliza cuando un admin blanquea la contraseña de un usuario,
     * el sistema obliga a cambiar la contraseña al iniciar sesión nuevamente.
     * Contiene el campo de nueva contraseña.
     */

    private Long userId; // ID del usuario cuyo password se va a restablecer

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
