package com.poo.miapi.module.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poo.miapi.module.auth.enums.AuthTokenType;
import com.poo.miapi.module.auth.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    /**
     * El único token vigente del usuario para ese flujo. Se busca por usuario y tipo (no por
     * hash) porque el código de verificación es de 6 dígitos y el hash está mezclado con el
     * id del usuario: para recalcularlo hay que saber de quién es.
     */
    Optional<AuthToken> findByUserIdAndTokenTypeAndUsedFalse(UUID userId, AuthTokenType tokenType);

    Optional<AuthToken> findByTokenHash(String tokenHash);

    /** Un reenvío invalida lo anterior: no deben quedar dos códigos válidos a la vez. */
    @Modifying
    @Query("update AuthToken t set t.used = true where t.userId = :userId and t.tokenType = :type and t.used = false")
    int invalidatePending(@Param("userId") UUID userId, @Param("type") AuthTokenType type);

    /** Higiene: los expirados no sirven para nada y la tabla no debe crecer sin límite. */
    @Modifying
    @Query("delete from AuthToken t where t.expiresAt < :before")
    int deleteExpiredBefore(@Param("before") LocalDateTime before);
}
