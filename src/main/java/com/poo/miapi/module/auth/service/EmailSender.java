package com.poo.miapi.module.auth.service;

/**
 * Puerto de salida para los mails de auth. Se define como interfaz para que los flujos
 * queden completos y verificables sin decidir todavía el proveedor: conectar SMTP es
 * agregar una implementación, sin tocar AuthService.
 */
public interface EmailSender {

    /** Código de 6 dígitos que activa la cuenta. */
    void sendVerificationCode(String email, String code);

    /** Token de recuperación, para armar el enlace. */
    void sendPasswordReset(String email, String token);
}
