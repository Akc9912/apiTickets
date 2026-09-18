package com.poo.miapi.module.auth.api;

import com.poo.miapi.module.auth.api.dto.request.*;
import com.poo.miapi.module.auth.api.dto.response.LoginResponse;
import com.poo.miapi.module.auth.api.dto.response.TokenResponse;

/**
 * Contrato de entrada al módulo auth. Los consumidores dependen de esta interfaz, no de
 * AuthService.
 *
 * Ningún método recibe el id del usuario sobre el que opera: o sale del token de acceso
 * (changePassword), o del token del propio flujo (resetPassword), o del email más una prueba
 * de posesión (verifyEmail). Un id en el body sería una puerta para operar sobre otra cuenta.
 *
 * Errores: {@link IllegalArgumentException} para entrada inválida, credenciales incorrectas o
 * tokens vencidos, y {@link jakarta.persistence.EntityNotFoundException} cuando el usuario no
 * existe. GlobalExceptionHandler los mapea a 400 y 404.
 */
public interface AuthApi {

    /**
     * Alta de cuenta. Crea el usuario en PENDING_VERIFICATION, emite el código y lo manda por
     * mail. No hay login automático: hasta verificar, la cuenta no puede iniciar sesión.
     */
    void register(RegisterRequest request);

    /** Consume el código y activa la cuenta. */
    void verifyEmail(VerifyEmailRequest request);

    /** Reemite el código e invalida el anterior. Respuesta genérica: no revela si existe. */
    void resendVerificationCode(ResendCodeRequest request);

    /** Autentica y devuelve el par de tokens más el perfil. Sólo cuentas ACTIVE. */
    LoginResponse login(LoginRequest request);

    /** Rota el refresh token. Reusar uno ya rotado revoca todas las sesiones del usuario. */
    TokenResponse refresh(RefreshRequest request);

    /** Revoca el refresh presentado. Idempotente. */
    void logout(RefreshRequest request);

    /**
     * Inicia la recuperación de contraseña. **No informa si el email existe**: siempre termina
     * igual, para no convertir el endpoint en un verificador de qué cuentas están registradas.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /** Consume el token de recuperación, cambia la contraseña y cierra todas las sesiones. */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Cambia la contraseña del usuario autenticado. Exige la actual y cierra las demás
     * sesiones.
     *
     * @param email del token de acceso, nunca del body
     */
    void changePassword(String email, ChangePasswordRequest request);
}
