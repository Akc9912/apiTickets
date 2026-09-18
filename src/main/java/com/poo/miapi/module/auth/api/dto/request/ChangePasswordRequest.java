package com.poo.miapi.module.auth.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sin userId, a propósito: el usuario sale del token del request. La versión anterior lo
 * recibía en el body y, combinada con /api/auth/** en permitAll, permitía a cualquiera sin
 * autenticarse cambiar la contraseña de cualquier usuario.
 *
 * Y sí pide la contraseña actual: antes no se verificaba.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String newPassword;
}
