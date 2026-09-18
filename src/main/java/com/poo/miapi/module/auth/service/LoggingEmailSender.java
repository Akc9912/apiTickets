package com.poo.miapi.module.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementación de desarrollo: no manda nada, escribe el código en el log para poder seguir
 * el flujo de punta a punta.
 *
 * NO usar fuera de desarrollo. Escribe en el log el secreto que debería viajar sólo al buzón
 * del usuario, así que cualquiera con acceso a los logs puede activar cuentas y resetear
 * contraseñas. Al conectar un proveedor real, esta clase se reemplaza, no se complementa.
 */
@Service
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    public LoggingEmailSender() {
        log.warn("EmailSender de desarrollo activo: los mails NO se envían y los códigos "
                + "quedan escritos en el log. No usar en producción.");
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        log.info("[email simulado] verificación para {} -> código {}", email, code);
    }

    @Override
    public void sendPasswordReset(String email, String token) {
        log.info("[email simulado] recuperación para {} -> token {}", email, token);
    }
}
