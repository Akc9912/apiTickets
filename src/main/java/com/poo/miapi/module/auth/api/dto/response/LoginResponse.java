package com.poo.miapi.module.auth.api.dto.response;

import com.poo.miapi.module.users.api.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del login: los tokens más el perfil, para que el cliente no tenga que pedirlo
 * aparte. El perfil se reutiliza de users (UserResponse) en lugar de duplicar el DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private TokenResponse tokens;
    private UserResponse user;
}
