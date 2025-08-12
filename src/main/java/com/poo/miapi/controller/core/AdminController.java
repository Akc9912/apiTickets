package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.service.core.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administradores", description = "Endpoints para gestión administrativa del sistema")
public class AdminController {
        private final AdminService adminService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdminController.class);

        public AdminController(AdminService adminService) {
                this.adminService = adminService;
        }

    // POST /api/admin/usuarios - Crear un nuevo usuario (admin, técnico,
    // trabajador)
    @PostMapping("/usuarios")
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema (admin, técnico, trabajador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el usuario"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    public ResponseEntity<UsuarioResponseDto> crearUsuario(
            @Parameter(description = "Datos del nuevo usuario") @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        logger.info("[AdminController] POST /usuarios datos: {}", usuarioDto);
        UsuarioResponseDto resp = adminService.crearUsuario(usuarioDto);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/usuarios - Listar todos los usuarios
    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        logger.info("[AdminController] GET /usuarios");
        List<UsuarioResponseDto> resp = adminService.listarTodosLosUsuarios();
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/usuarios/{id} - Ver detalles de un usuario
    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Ver detalles de usuario", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> verUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] GET /usuarios/{}", id);
        UsuarioResponseDto resp = adminService.verUsuarioPorId(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id} - Editar datos de un usuario
    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Editar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<UsuarioResponseDto> editarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id,
            @Parameter(description = "Nuevos datos del usuario") @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        logger.info("[AdminController] PUT /usuarios/{} datos: {}", id, usuarioDto);
        UsuarioResponseDto resp = adminService.editarUsuario(id, usuarioDto);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/activar - Activar usuario
    @PutMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar usuario", description = "Activa un usuario desactivado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> activarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] PUT /usuarios/{}/activar", id);
        UsuarioResponseDto resp = adminService.activarUsuario(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/desactivar - Desactivar usuario
    @PutMapping("/usuarios/{id}/desactivar")
    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario activo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] PUT /usuarios/{}/desactivar", id);
        UsuarioResponseDto resp = adminService.desactivarUsuario(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/bloquear - Bloquear usuario
    @PutMapping("/usuarios/{id}/bloquear")
    @Operation(summary = "Bloquear usuario", description = "Bloquea un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario bloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] PUT /usuarios/{}/bloquear", id);
        UsuarioResponseDto resp = adminService.bloquearUsuario(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/desbloquear - Desbloquear usuario
    @PutMapping("/usuarios/{id}/desbloquear")
    @Operation(summary = "Desbloquear usuario", description = "Desbloquea un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desbloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> desbloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] PUT /usuarios/{}/desbloquear", id);
        UsuarioResponseDto resp = adminService.desbloquearUsuario(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // POST /api/admin/usuarios/{id}/reset-password - Resetear contraseña de usuario
    @PostMapping("/usuarios/{id}/reset-password")
    @Operation(summary = "Resetear contraseña", description = "Resetea la contraseña de un usuario a la por defecto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña reseteada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> resetearPassword(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[AdminController] POST /usuarios/{}/reset-password", id);
        UsuarioResponseDto resp = adminService.blanquearPassword(id);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/rol - Cambiar el rol de un usuario
    @PutMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id,
            @Parameter(description = "Datos del nuevo rol") @Valid @RequestBody UsuarioRequestDto cambiarRolDto) {
        logger.info("[AdminController] PUT /usuarios/{}/rol datos: {}", id, cambiarRolDto);
        UsuarioResponseDto resp = adminService.cambiarRolUsuario(id, cambiarRolDto);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/tickets - Listar todos los tickets del sistema
    @GetMapping("/tickets")
    @Operation(summary = "Listar todos los tickets", description = "Obtiene una lista de todos los tickets del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente")
    })
    public ResponseEntity<List<TicketResponseDto>> listarTickets() {
        logger.info("[AdminController] GET /tickets");
        List<TicketResponseDto> resp = adminService.listarTodosLosTickets();
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // POST /api/admin/tickets/{id}/reabrir - Reabrir un ticket cerrado
    @PostMapping("/tickets/{id}/reabrir")
    @Operation(summary = "Reabrir ticket", description = "Reabre un ticket que fue cerrado previamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket reabierto exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "El ticket no puede ser reabierto")
    })
    public ResponseEntity<TicketResponseDto> reabrirTicket(
            @Parameter(description = "ID del ticket") @PathVariable int id,
            @Parameter(description = "Comentario sobre la reapertura") @RequestParam String comentario) {
        logger.info("[AdminController] POST /tickets/{}/reabrir comentario: {}", id, comentario);
        TicketResponseDto resp = adminService.reabrirTicket(id, comentario);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/usuarios/rol?rol=TECNICO - Listar usuarios por rol
    @GetMapping("/usuarios/rol")
    @Operation(summary = "Listar usuarios por rol", description = "Obtiene una lista de usuarios filtrados por rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios por rol obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorRol(
            @Parameter(description = "Rol a filtrar (ADMIN, TECNICO, TRABAJADOR, SUPERADMIN)") @RequestParam String rol) {
        logger.info("[AdminController] GET /usuarios/rol rol: {}", rol);
        List<UsuarioResponseDto> resp = adminService.listarUsuariosPorRol(rol);
        logger.info("[AdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

}
