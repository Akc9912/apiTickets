package com.poo.miapi.module.auth.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Par de tokens. El access token es un JWT de vida corta y no se puede revocar; el refresh
 * es opaco, revocable y rota en cada uso.
 *
 * expiresInSeconds se refiere al access token: el cliente lo necesita para saber cuándo
 * pedir la rotación sin tener que decodificar el JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresInSeconds;
}
