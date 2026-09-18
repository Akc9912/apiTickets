package com.poo.miapi.module.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poo.miapi.module.auth.enums.AuthTokenType;
import com.poo.miapi.module.auth.model.AuthToken;
import com.poo.miapi.module.auth.model.RefreshToken;
import com.poo.miapi.module.auth.repository.AuthTokenRepository;
import com.poo.miapi.module.auth.repository.RefreshTokenRepository;

/**
 * Emisión, validación y rotación de los tokens que sí se persisten.
 *
 * Regla común: en la base vive sólo el SHA-256 del token, nunca el valor. El valor en claro
 * se devuelve una única vez al emitirlo y no se puede recuperar después.
 *
 * Los dos flujos hashean distinto, a propósito:
 *
 * - VERIFICATION es un código de 6 dígitos, o sea un millón de valores posibles. Dos usuarios
 *   pueden sacar el mismo y colisionarían contra el UNIQUE de token_hash, así que el hash se
 *   calcula sobre userId + ":" + código. Consecuencia: para validar hay que saber de quién es
 *   el código, y por eso verify-email pide el email además del código.
 * - PASSWORD_RESET es un token aleatorio de 256 bits, globalmente único. Se hashea solo, sin
 *   mezclar el usuario, así se puede buscar directo por hash y el enlace del mail se sostiene
 *   por sí mismo.
 */
@Service
public class TokenService {

    private static final int REFRESH_TOKEN_BYTES = 32;   // 256 bits
    private static final int RESET_TOKEN_BYTES = 32;

    private final AuthTokenRepository authTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom random = new SecureRandom();

    private final Duration verificationTtl;
    private final Duration passwordResetTtl;
    private final Duration refreshTtl;
    private final int maxAttempts;

    public TokenService(
            AuthTokenRepository authTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${auth.verification-code-expiration-minutes}") long verificationMinutes,
            @Value("${auth.password-reset-expiration-hours}") long passwordResetHours,
            @Value("${jwt.refresh-token-expiration-days}") long refreshDays,
            @Value("${auth.max-verification-attempts}") int maxAttempts) {
        this.authTokenRepository = authTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationTtl = Duration.ofMinutes(verificationMinutes);
        this.passwordResetTtl = Duration.ofHours(passwordResetHours);
        this.refreshTtl = Duration.ofDays(refreshDays);
        this.maxAttempts = maxAttempts;
    }

    /* ---------- verificación de cuenta ---------- */

    /**
     * Emite el código de 6 dígitos e invalida cualquiera anterior del usuario: no deben
     * quedar dos códigos válidos a la vez.
     *
     * @return el código en claro, para mandarlo por mail. No vuelve a estar disponible.
     */
    @Transactional
    public String issueVerificationCode(UUID userId) {
        authTokenRepository.invalidatePending(userId, AuthTokenType.VERIFICATION);
        String code = randomSixDigitCode();
        persist(userId, AuthTokenType.VERIFICATION, hashFor(userId, code), verificationTtl);
        return code;
    }

    /**
     * Consume el código. Un código equivocado cuenta como intento fallido y al llegar al
     * máximo el token se quema, para que no se pueda recorrer el espacio de 6 dígitos.
     *
     * noRollbackFor no es decorativo: el incremento se guarda y recién después se lanza la
     * excepción, así que con el rollback por defecto el contador volvería a cero en cada
     * intento y el máximo nunca se alcanzaría. El rechazo y el conteo son los dos efectos
     * buscados, no un efecto y un error.
     *
     * @throws IllegalArgumentException si no hay código vigente, si expiró o si no coincide
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void consumeVerificationCode(UUID userId, String code) {
        AuthToken token = authTokenRepository
                .findByUserIdAndTokenTypeAndUsedFalse(userId, AuthTokenType.VERIFICATION)
                .orElseThrow(() -> new IllegalArgumentException("No hay un código de verificación pendiente"));

        if (!token.isUsable()) {
            throw new IllegalArgumentException("El código de verificación expiró");
        }

        if (!constantTimeEquals(token.getTokenHash(), hashFor(userId, code))) {
            token.setAttempts((short) (token.getAttempts() + 1));
            if (token.getAttempts() >= maxAttempts) {
                // Quemado: hay que pedir uno nuevo.
                token.setUsed(true);
            }
            authTokenRepository.save(token);
            throw new IllegalArgumentException("Código de verificación incorrecto");
        }

        token.setUsed(true);
        authTokenRepository.save(token);
    }

    /* ---------- recuperación de contraseña ---------- */

    /** @return el token en claro, para armar el enlace del mail. */
    @Transactional
    public String issuePasswordResetToken(UUID userId) {
        authTokenRepository.invalidatePending(userId, AuthTokenType.PASSWORD_RESET);
        String token = randomUrlSafeToken(RESET_TOKEN_BYTES);
        persist(userId, AuthTokenType.PASSWORD_RESET, sha256Hex(token), passwordResetTtl);
        return token;
    }

    /**
     * Consume el token de recuperación y devuelve de qué usuario era. Acá sí se busca por
     * hash porque el token es globalmente único: el enlace no necesita llevar el email.
     *
     * @throws IllegalArgumentException si el token no existe, ya se usó o expiró
     */
    @Transactional
    public UUID consumePasswordResetToken(String token) {
        AuthToken found = authTokenRepository.findByTokenHash(sha256Hex(token))
                .filter(t -> t.getTokenType() == AuthTokenType.PASSWORD_RESET)
                .orElseThrow(() -> new IllegalArgumentException("Token de recuperación inválido"));

        if (!found.isUsable()) {
            throw new IllegalArgumentException("El token de recuperación ya fue usado o expiró");
        }

        found.setUsed(true);
        authTokenRepository.save(found);
        return found.getUserId();
    }

    /* ---------- refresh tokens ---------- */

    /** @return el refresh token en claro. */
    @Transactional
    public String issueRefreshToken(UUID userId) {
        String token = randomUrlSafeToken(REFRESH_TOKEN_BYTES);
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .tokenHash(sha256Hex(token))
                .expiresAt(LocalDateTime.now().plus(refreshTtl))
                .build());
        return token;
    }

    /**
     * Rota el refresh token: revoca el presentado y emite uno nuevo.
     *
     * Si el token presentado ya estaba revocado, es señal de que alguien está reusando uno
     * viejo — o el legítimo con un token robado, o el atacante con el token del legítimo. No
     * hay forma de distinguirlos, así que se revocan **todas** las sesiones del usuario y los
     * dos tienen que volver a autenticarse. Perder la sesión es preferible a dejarla en manos
     * de quien la robó.
     *
     * Igual que en la verificación, la revocación en cascada se persiste y después se lanza
     * la excepción: con el rollback por defecto se desharía y la defensa contra el reuso no
     * haría absolutamente nada.
     *
     * @throws IllegalArgumentException si el token no existe, expiró o fue reusado
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public Rotation rotateRefreshToken(String token) {
        RefreshToken existing = refreshTokenRepository.findByTokenHash(sha256Hex(token))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

        if (existing.isRevoked()) {
            revokeAllForUser(existing.getUserId());
            throw new IllegalArgumentException(
                    "Refresh token ya utilizado: se revocaron todas las sesiones del usuario");
        }

        if (!existing.isUsable()) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        existing.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(existing);

        UUID userId = existing.getUserId();
        return new Rotation(userId, issueRefreshToken(userId));
    }

    /** Revoca un único refresh token. Silencioso si no existe: el logout es idempotente. */
    @Transactional
    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash(sha256Hex(token));
        found.filter(t -> !t.isRevoked()).ifPresent(t -> {
            t.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(t);
        });
    }

    /** Cierra todas las sesiones: reuso detectado, cambio o reset de contraseña. */
    @Transactional
    public int revokeAllForUser(UUID userId) {
        return refreshTokenRepository.revokeAllForUser(userId, LocalDateTime.now());
    }

    /** Resultado de una rotación: de quién es la sesión y el token nuevo en claro. */
    public record Rotation(UUID userId, String refreshToken) {
    }

    /* ---------- helpers ---------- */

    private void persist(UUID userId, AuthTokenType type, String hash, Duration ttl) {
        authTokenRepository.save(AuthToken.builder()
                .userId(userId)
                .tokenType(type)
                .tokenHash(hash)
                .expiresAt(LocalDateTime.now().plus(ttl))
                .build());
    }

    /** Seis dígitos con relleno de ceros: 000000 es tan válido como 999999. */
    private String randomSixDigitCode() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String randomUrlSafeToken(int bytes) {
        byte[] buffer = new byte[bytes];
        random.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    private String hashFor(UUID userId, String code) {
        return sha256Hex(userId + ":" + code);
    }

    /**
     * SHA-256 hex: 64 caracteres, que es exactamente el largo de la columna. No se usa BCrypt
     * aunque next_steps.md lo sugiera: BCrypt saltea, así que da un hash distinto cada vez y
     * hace imposible tanto el UNIQUE de la columna como buscar por hash.
     */
    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en la JVM", e);
        }
    }

    /** Comparación en tiempo constante: no filtrar cuántos caracteres coincidieron. */
    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
