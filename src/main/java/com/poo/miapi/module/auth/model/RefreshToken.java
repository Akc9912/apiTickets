package com.poo.miapi.module.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

/**
 * Refresh token opaco. El valor que ve el cliente es aleatorio de 256 bits; acá vive sólo
 * su SHA-256, igual que en AuthToken. Al ser globalmente único no necesita mezclarse con el
 * id del usuario: se puede buscar directo por hash.
 *
 * No hay columna is_active: un token está vigente si revokedAt es null y no expiró.
 * Mantener un booleano aparte solo agrega una fuente de verdad que se desincroniza.
 *
 * Tampoco hay columna de linaje. La detección de reuso se resuelve revocando todos los
 * tokens del usuario, que no necesita saber la cadena; si algún día se quiere granularidad
 * por familia, hay que agregar la columna.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    @Builder.Default
    private LocalDateTime revokedAt = null;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onPersist() {
        createdAt = LocalDateTime.now();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isUsable() {
        return !isRevoked() && expiresAt.isAfter(LocalDateTime.now());
    }
}
