package com.poo.miapi.controller.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.service.auth.AuthService;
import com.poo.miapi.service.auditoria.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de contraseñas")
public class AuthController {

    private final AuthService authService;
    private final AuditoriaService auditoriaService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, AuditoriaService auditoriaService) {
        this.authService = authService;
        this.auditoriaService = auditoriaService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Credenciales de login") @RequestBody @Valid LoginRequestDto request,
            HttpServletRequest httpRequest) {
        logger.info("[AuthController] POST /login datos: {}", request);

        try {
            LoginResponseDto response = authService.login(request);

            // Auditar login exitoso
            auditoriaService.registrarAccion(
                    null, // No tenemos el usuario aún
                    AccionAuditoria.LOGIN,
                    "USUARIO",
                    null,
                    "Login exitoso para: " + request.getEmail(),
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.LOW);

            logger.info("[AuthController] Respuesta: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Auditar login fallido
            auditoriaService.registrarAccion(
                    null,
                    AccionAuditoria.LOGIN_FAILED,
                    "USUARIO",
                    null,
                    "Login fallido para: " + request.getEmail() + " - Motivo: " + ex.getMessage(),
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.HIGH);

            logger.warn("[AuthController] Login fallido: {}", ex.getMessage());
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Credenciales inválidas"));
        }
    }

    // POST /api/auth/cambiar-password
    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario cambiar su contraseña actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> cambiarPassword(
            @Parameter(description = "Datos para cambiar la contraseña") @RequestBody @Valid ChangePasswordDto dto,
            HttpServletRequest httpRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        logger.info("[AuthController] POST /cambiar-password datos: {}", dto);

        try {
            authService.cambiarPassword(dto);

            // Auditar cambio de contraseña
            auditoriaService.registrarAccion(
                    usuarioAutenticado,
                    AccionAuditoria.CHANGE_PASSWORD,
                    "USUARIO",
                    dto.getUserId(),
                    "Cambio de contraseña exitoso",
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.MEDIUM);

            logger.info("[AuthController] Contraseña actualizada correctamente");
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (Exception ex) {
            // Auditar fallo en cambio de contraseña
            auditoriaService.registrarAccion(
                    usuarioAutenticado,
                    AccionAuditoria.CHANGE_PASSWORD,
                    "USUARIO",
                    dto.getUserId(),
                    "Fallo en cambio de contraseña: " + ex.getMessage(),
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.HIGH);
            throw ex;
        }
    }

    // POST /api/auth/reiniciar-password
    @PostMapping("/reiniciar-password")
    @Operation(summary = "Reiniciar contraseña", description = "Reinicia la contraseña de un usuario (funcionalidad administrativa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña reiniciada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> reiniciarPassword(
            @Parameter(description = "Datos para reiniciar la contraseña") @RequestBody @Valid ResetPasswordDto dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado,
            HttpServletRequest httpRequest) {
        logger.info("[AuthController] POST /reiniciar-password datos: {}", dto);

        if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null) {
            return ResponseEntity.status(403).body("No autorizado");
        }
        Rol rol = usuarioAutenticado.getRol();
        if (!rol.canManageUsers()) {
            return ResponseEntity.status(403).body("Solo admin o superadmin pueden reiniciar contraseñas");
        }

        try {
            authService.reiniciarPassword(dto);

            // Auditar reinicio de contraseña
            auditoriaService.registrarAccion(
                    usuarioAutenticado,
                    AccionAuditoria.RESET_PASSWORD,
                    "USUARIO",
                    dto.getUserId(),
                    "Reinicio de contraseña exitoso por admin",
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.HIGH);

            logger.info("[AuthController] Contraseña reiniciada correctamente");
            return ResponseEntity.ok("Contraseña reiniciada correctamente");
        } catch (Exception ex) {
            // Auditar fallo en reinicio de contraseña
            auditoriaService.registrarAccion(
                    usuarioAutenticado,
                    AccionAuditoria.RESET_PASSWORD,
                    "USUARIO",
                    dto.getUserId(),
                    "Fallo en reinicio de contraseña: " + ex.getMessage(),
                    null,
                    null,
                    CategoriaAuditoria.SECURITY,
                    SeveridadAuditoria.CRITICAL);
            throw ex;
        }
    }
}
