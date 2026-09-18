package com.poo.miapi.module.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poo.miapi.module.auth.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Busca por hash sin filtrar revocados ni expirados a propósito: el que rota necesita
     * distinguir "no existe" de "existe pero ya fue usado". Lo segundo es señal de robo y
     * dispara la revocación en cascada; si la consulta los escondiera, se perdería la señal.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Revoca todas las sesiones del usuario: reuso detectado, cambio o reset de contraseña. */
    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :now where t.userId = :userId and t.revokedAt is null")
    int revokeAllForUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("delete from RefreshToken t where t.expiresAt < :before")
    int deleteExpiredBefore(@Param("before") LocalDateTime before);
}
