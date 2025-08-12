package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.service.core.SuperAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/superadmin")
@Tag(name = "SuperAdmin", description = "Endpoints exclusivos del SuperAdmin - Dueño del sistema con acceso total")
@SecurityRequirement(name = "bearerAuth")
public class SuperAdminController {
        private final SuperAdminService superAdminService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SuperAdminController.class);

        public SuperAdminController(SuperAdminService superAdminService) {
                this.superAdminService = superAdminService;
        }


    // === GESTIÓN DE ADMINISTRADORES ===

    @Operation(summary = "Listar administradores", description = "Obtener lista de todos los administradores del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida exitosamente")
    })
    @GetMapping("/admins")
    public ResponseEntity<List<UsuarioResponseDto>> listarAdministradores() {
        logger.info("[SuperAdminController] GET /admins");
        List<UsuarioResponseDto> resp = superAdminService.listarAdministradores();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Promover a administrador", description = "Promover un usuario a rol de administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario promovido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "El usuario ya es administrador")
    })
    @PostMapping("/admins/{id}/promover")
    public ResponseEntity<UsuarioResponseDto> promoverAAdmin(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] POST /admins/{}/promover", id);
        UsuarioResponseDto resp = superAdminService.promoverAAdmin(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Degradar administrador", description = "Degradar un administrador a rol de trabajador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador degradado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "El usuario no es administrador")
    })
    @PostMapping("/admins/{id}/degradar")
    public ResponseEntity<UsuarioResponseDto> degradarAdmin(
            @Parameter(description = "ID del administrador") @PathVariable int id) {
        logger.info("[SuperAdminController] POST /admins/{}/degradar", id);
        UsuarioResponseDto resp = superAdminService.degradarAdmin(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }
}
