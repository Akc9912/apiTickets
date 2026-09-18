package com.poo.miapi.module.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Eventos de seguridad, con los nombres definidos en
 * docs/iteracion-02-.../fase-1-hardening-auth-seguro/1.3.md.
 *
 * Regla que se respeta acá: no se registran secretos ni tokens completos. Del email se
 * guarda el valor porque es el actor del evento y sin él la traza no sirve; de los tokens no
 * se guarda nada, ni siquiera un prefijo.
 *
 * Va a un logger propio ("security") para poder enrutarlo a logs/security sin mezclarlo con
 * el log de la aplicación. No hay tabla de auditoría: no está decidida.
 */
@Service
public class SecurityAuditLog {

    private static final Logger log = LoggerFactory.getLogger("security");

    public void loginSuccess(String email) {
        log.info("event=login_success actor={}", email);
    }

    public void loginFailed(String email, String reason) {
        log.warn("event=login_failed actor={} reason={}", email, reason);
    }

    public void recoveryRequested(String email) {
        log.info("event=recovery_requested actor={}", email);
    }

    public void recoveryConfirmed(String email) {
        log.info("event=recovery_confirmed actor={}", email);
    }

    public void rateLimitTriggered(String email, String endpoint) {
        log.warn("event=rate_limit_triggered actor={} endpoint={}", email, endpoint);
    }

    /** Reuso de un refresh token: se revocaron todas las sesiones. */
    public void refreshTokenReuseDetected(String email) {
        log.warn("event=refresh_token_reuse actor={} action=all_sessions_revoked", email);
    }
}
