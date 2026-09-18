package com.poo.miapi.module.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.module.auth.api.AuthApi;
import com.poo.miapi.module.auth.api.dto.request.*;
import com.poo.miapi.module.auth.api.dto.response.LoginResponse;
import com.poo.miapi.module.auth.api.dto.response.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

/**
 * Autenticación. Prefijo /api/auth/v1 para seguir el patrón /api/{scope}/v1 que ya usa users.
 *
 * Casi todo es público por necesidad, pero **no todo**: logout y change-password exigen
 * autenticación. La versión anterior dejaba /api/auth/** entero en permitAll y eso, combinado
 * con un userId en el body, permitía cambiar la contraseña de cualquier usuario sin
 * autenticarse. Si agregás un endpoint acá, decidí explícitamente en qué grupo va.
 */
@RestController
@RequestMapping("/api/auth/v1")
@Tag(name = "Authentication", description = "Registration, login, token rotation and password management")
public class AuthController {

    private static final String ANY_ROLE = "hasAnyRole('USER','ADMIN','SUPERADMIN')";

    private final AuthApi authApi;

    public AuthController(AuthApi authApi) {
        this.authApi = authApi;
    }

    /* ---------- público ---------- */

    // POST /api/auth/v1/register
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Creates an account in PENDING_VERIFICATION and emails a 6-digit code. There is no automatic login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Account created; verification code sent"),
            @ApiResponse(responseCode = "400", description = "Invalid data or email already registered")
    })
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authApi.register(request);
        // 202 y no 201: la cuenta existe pero todavía no sirve para operar.
        return ResponseEntity.accepted().build();
    }

    // POST /api/auth/v1/verify-email
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Consumes the 6-digit code and activates the account. Wrong codes count as attempts; the code burns after the configured maximum.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account verified"),
            @ApiResponse(responseCode = "400", description = "Wrong, expired or already used code"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {
        authApi.verifyEmail(request);
        return ResponseEntity.noContent().build();
    }

    // POST /api/auth/v1/resend-code
    @PostMapping("/resend-code")
    @Operation(summary = "Resend verification code", description = "Issues a new code and invalidates the previous one. Always 202, whether or not the account exists.")
    @ApiResponses(@ApiResponse(responseCode = "202", description = "Request accepted"))
    public ResponseEntity<Void> resendCode(@RequestBody @Valid ResendCodeRequest request) {
        authApi.resendVerificationCode(request);
        return ResponseEntity.accepted().build();
    }

    // POST /api/auth/v1/login
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Returns an access token plus a refresh token. Only ACTIVE accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authenticated", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or account not enabled"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authApi.login(request));
    }

    // POST /api/auth/v1/refresh
    @PostMapping("/refresh")
    @Operation(summary = "Refresh tokens", description = "Rotates the refresh token. Reusing an already rotated token revokes every session of that user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New token pair", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid, expired or reused refresh token")
    })
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        return ResponseEntity.ok(authApi.refresh(request));
    }

    // POST /api/auth/v1/forgot-password
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Emails a recovery token. Always 202, whether or not the email is registered, so the endpoint cannot be used to discover accounts.")
    @ApiResponses(@ApiResponse(responseCode = "202", description = "Request accepted"))
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authApi.forgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    // POST /api/auth/v1/reset-password
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Consumes the recovery token, sets the new password and revokes every session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "Invalid, expired or already used token")
    })
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authApi.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /* ---------- autenticado ---------- */

    // POST /api/auth/v1/logout
    @PostMapping("/logout")
    @PreAuthorize(ANY_ROLE)
    @Operation(summary = "Logout", description = "Revokes the presented refresh token. Idempotent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session closed"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Missing role")
    })
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshRequest request) {
        authApi.logout(request);
        return ResponseEntity.noContent().build();
    }

    // POST /api/auth/v1/change-password
    @PostMapping("/change-password")
    @PreAuthorize(ANY_ROLE)
    @Operation(summary = "Change password", description = "Requires the current password. The user is taken from the access token, never from the body. Revokes every session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "Wrong current password, or new password equal to the current one"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Missing role")
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody @Valid ChangePasswordRequest request) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        // La identidad sale del token. El DTO ya no tiene userId justamente para que no haya
        // forma de apuntar a otra cuenta desde el body.
        authApi.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
