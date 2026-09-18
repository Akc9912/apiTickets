package com.poo.miapi.module.auth.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poo.miapi.module.auth.api.AuthApi;
import com.poo.miapi.module.auth.api.dto.request.*;
import com.poo.miapi.module.auth.api.dto.response.LoginResponse;
import com.poo.miapi.module.auth.api.dto.response.TokenResponse;
import com.poo.miapi.module.auth.security.UserPrincipal;
import com.poo.miapi.module.users.api.UserApi;
import com.poo.miapi.module.users.api.dto.request.CreateUserRequest;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService implements AuthApi {

    /**
     * Hash de descarte para igualar el tiempo de respuesta cuando el email no existe. Sin
     * esto, un login contra una cuenta inexistente vuelve mucho más rápido que uno con la
     * contraseña equivocada, y esa diferencia alcanza para enumerar usuarios.
     */
    private static final String DUMMY_HASH =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UserApi userApi;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final SecurityAuditLog audit;

    public AuthService(UserApi userApi, TokenService tokenService, JwtService jwtService,
            PasswordEncoder passwordEncoder, EmailSender emailSender, SecurityAuditLog audit) {
        this.userApi = userApi;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.audit = audit;
    }

    /* ---------- registro y verificación ---------- */

    /**
     * Deliberadamente SIN @Transactional. Compone tres pasos que ya son transaccionales por
     * separado (crear el usuario, emitir el código) más un efecto externo irreversible
     * (mandar el mail). Si todo estuviera en una sola transacción, el mail saldría antes del
     * commit y un rollback dejaría un código notificado para un usuario que no existe.
     *
     * El precio es que no es atómico: puede quedar un usuario creado sin código emitido. Es
     * un estado recuperable — resend-code lo resuelve — y preferible a mandar mails sobre
     * altas que después se deshacen.
     */
    @Override
    public void register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }

        userApi.create(CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build());

        // create() no devuelve nada, así que el id se resuelve con una lectura extra.
        User user = userApi.findByEmail(request.getEmail());
        String code = tokenService.issueVerificationCode(user.getId());
        emailSender.sendVerificationCode(user.getEmail(), code);
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userApi.findByEmail(request.getEmail());

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("La cuenta ya está verificada");
        }

        tokenService.consumeVerificationCode(user.getId(), request.getCode());
        userApi.changeStatus(user.getId(), UserStatus.ACTIVE);
    }

    /**
     * No distingue si el email existe ni si la cuenta ya estaba verificada: termina igual en
     * los tres casos. Es el mismo criterio que forgotPassword.
     */
    @Override
    public void resendVerificationCode(ResendCodeRequest request) {
        try {
            User user = userApi.findByEmail(request.getEmail());
            if (user.getStatus() == UserStatus.ACTIVE) {
                return;
            }
            String code = tokenService.issueVerificationCode(user.getId());
            emailSender.sendVerificationCode(user.getEmail(), code);
        } catch (EntityNotFoundException e) {
            // Silencio intencional.
        }
    }

    /* ---------- login y tokens ---------- */

    @Override
    public LoginResponse login(LoginRequest request) {
        User user;
        try {
            user = userApi.findByEmail(request.getEmail());
        } catch (EntityNotFoundException e) {
            passwordEncoder.matches(request.getPassword(), DUMMY_HASH);
            audit.loginFailed(request.getEmail(), "user_not_found");
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            audit.loginFailed(request.getEmail(), "bad_password");
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // El motivo sí se distingue acá: ya probó que sabe la contraseña, así que decirle que
        // le falta verificar el mail no le revela nada que no sepa, y sin eso no sabría qué hacer.
        if (user.getStatus() != UserStatus.ACTIVE) {
            audit.loginFailed(request.getEmail(), "status_" + user.getStatus());
            throw new IllegalArgumentException(
                    user.getStatus() == UserStatus.PENDING_VERIFICATION
                            ? "La cuenta todavía no fue verificada"
                            : "La cuenta no está habilitada");
        }

        audit.loginSuccess(user.getEmail());
        return LoginResponse.builder()
                .tokens(issueTokens(user))
                .user(userApi.getById(user.getId()))
                .build();
    }

    @Override
    public TokenResponse refresh(RefreshRequest request) {
        TokenService.Rotation rotation;
        try {
            rotation = tokenService.rotateRefreshToken(request.getRefreshToken());
        } catch (IllegalArgumentException e) {
            // rotateRefreshToken ya revocó todo si detectó reuso; acá sólo se audita.
            audit.refreshTokenReuseDetected("(token no asociado a un email conocido)");
            throw e;
        }

        User user = userApi.findById(rotation.userId());
        if (user.getStatus() != UserStatus.ACTIVE) {
            tokenService.revokeAllForUser(user.getId());
            throw new IllegalArgumentException("La cuenta no está habilitada");
        }

        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(UserPrincipal.of(user)))
                .refreshToken(rotation.refreshToken())
                .expiresInSeconds(jwtService.getAccessTokenTtl().toSeconds())
                .build();
    }

    @Override
    public void logout(RefreshRequest request) {
        tokenService.revokeRefreshToken(request.getRefreshToken());
    }

    /* ---------- contraseñas ---------- */

    /**
     * Siempre termina sin error, exista o no la cuenta. Si respondiera distinto, cualquiera
     * podría usar este endpoint para averiguar qué emails están registrados.
     */
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        audit.recoveryRequested(request.getEmail());
        try {
            User user = userApi.findByEmail(request.getEmail());
            String token = tokenService.issuePasswordResetToken(user.getId());
            emailSender.sendPasswordReset(user.getEmail(), token);
        } catch (EntityNotFoundException e) {
            // Silencio intencional.
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        UUID userId = tokenService.consumePasswordResetToken(request.getToken());
        User user = userApi.findById(userId);

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userApi.save(user);

        // Quien tenía la contraseña vieja pierde sus sesiones: si el reset fue por robo de
        // cuenta, dejarle un refresh token vigente anularía el sentido de resetear.
        tokenService.revokeAllForUser(userId);
        audit.recoveryConfirmed(user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userApi.findByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            audit.loginFailed(email, "change_password_bad_current");
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña nueva no puede ser igual a la actual");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userApi.save(user);
        tokenService.revokeAllForUser(user.getId());
    }

    /* ---------- helpers ---------- */

    private TokenResponse issueTokens(User user) {
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(UserPrincipal.of(user)))
                .refreshToken(tokenService.issueRefreshToken(user.getId()))
                .expiresInSeconds(jwtService.getAccessTokenTtl().toSeconds())
                .build();
    }
}
