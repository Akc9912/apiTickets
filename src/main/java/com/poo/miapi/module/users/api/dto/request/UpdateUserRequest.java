package com.poo.miapi.module.users.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos editables del propio perfil. Los campos null se ignoran (update parcial).
 * No incluye email (requiere re-verificación), password (lo maneja AuthService),
 * ni globalRole/status (son operaciones de administración, no de perfil).
 */
// @NoArgsConstructor no es decorativo: @Builder suprime el constructor por defecto y sin
// él Jackson no puede deserializar el body (InvalidDefinitionException -> 500).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String phone;
}
