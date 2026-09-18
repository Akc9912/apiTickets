package com.poo.miapi.module.auth.enums;

/**
 * Discriminante de la tabla auth_tokens. Los dos flujos comparten tabla a propósito:
 * el ciclo de vida es idéntico (un solo uso, con expiración) y sólo cambia el TTL.
 */
public enum AuthTokenType {
    /** Código de 6 dígitos que activa la cuenta. Vida corta. */
    VERIFICATION,
    /** Token de recuperación de contraseña. Vida más larga. */
    PASSWORD_RESET
}
