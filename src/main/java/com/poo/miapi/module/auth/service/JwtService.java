package com.poo.miapi.module.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.auth.security.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emite y valida el access token. Es stateless: no se persiste y no se puede revocar, así
 * que la ventana de exposición la acota su expiración corta. La sesión larga la sostiene el
 * refresh token, que sí es revocable.
 */
@Service
public class JwtService {

    /** UUID del usuario. El subject queda para el email, que es el username del sistema. */
    public static final String CLAIM_USER_ID = "uid";
    /** Nombre en inglés, alineado con el resto de la API. El código viejo emitía "rol". */
    public static final String CLAIM_ROLE = "role";

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    /**
     * La validez del secreto se comprueba al construir el bean, no en cada firma: un secreto
     * corto tiene que impedir que la aplicación arranque, no fallar recién en el primer login.
     * HMAC-SHA256 exige 256 bits, así que menos de 32 caracteres no es aceptable.
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-minutes}") long accessTokenMinutes) {
        byte[] keyBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos 32 caracteres (256 bits) para HS256; "
                            + "tiene " + keyBytes.length + ". Configurá la variable JWT_SECRET.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim(CLAIM_USER_ID, principal.getId().toString())
                .claim(CLAIM_ROLE, principal.getGlobalRole() == null ? null : principal.getGlobalRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    /**
     * Devuelve los claims si la firma y la expiración son válidas, o null si el token no
     * sirve. Se devuelve null en lugar de propagar: para el filtro, un token inválido y uno
     * vencido se tratan igual.
     */
    private Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /** El email del token, o null si el token no es válido. */
    public String extractEmail(String token) {
        Claims claims = parse(token);
        return claims == null ? null : claims.getSubject();
    }

    public UUID extractUserId(String token) {
        Claims claims = parse(token);
        if (claims == null) {
            return null;
        }
        String raw = claims.get(CLAIM_USER_ID, String.class);
        try {
            return raw == null ? null : UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, String expectedEmail) {
        String email = extractEmail(token);
        return email != null && email.equals(expectedEmail);
    }
}
