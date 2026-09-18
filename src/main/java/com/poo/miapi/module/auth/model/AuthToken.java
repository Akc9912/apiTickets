package com.poo.miapi.module.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.poo.miapi.module.auth.enums.AuthTokenType;

import jakarta.persistence.*;
import lombok.*;

/**
 * Token de un solo uso para verificar la cuenta o recuperar la contraseña.
 *
 * Guarda el HASH, nunca el valor: si se filtra la base, los códigos en vuelo no sirven.
 * El hash es SHA-256 y no BCrypt, aunque next_steps.md sugiera BCrypt: BCrypt es salteado,
 * así que produce un hash distinto por cada cálculo y hace imposible tanto el UNIQUE de la
 * columna como buscar por hash.
 *
 * El hash se calcula sobre userId + ":" + código, no sobre el código solo. Un código de 6
 * dígitos tiene un millón de valores, así que dos usuarios pueden sacar el mismo y su hash
 * colisionaría contra uk_auth_tokens_hash. Mezclar el id del usuario lo evita.
 *
 * Referencia al usuario por UUID y no con @ManyToOne: auth no necesita navegar a la entidad
 * y así no se acopla al modelo de users, que se consume sólo por UserApi. La integridad la
 * mantiene la FK del esquema.
 */
@Entity
@Table(name = "auth_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private AuthTokenType tokenType;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    /** Intentos fallidos. Al llegar al máximo el token se quema, para cortar el brute force. */
    @Column(nullable = false)
    @Builder.Default
    private short attempts = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onPersist() {
        createdAt = LocalDateTime.now();
    }

    /** Vigente = sin usar y sin expirar. */
    public boolean isUsable() {
        return !used && expiresAt.isAfter(LocalDateTime.now());
    }
}
