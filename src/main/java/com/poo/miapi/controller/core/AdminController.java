package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
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
        return ResponseEntity.ok(adminService.crearUsuario(usuarioDto));
    }

    // GET /api/admin/usuarios - Listar todos los usuarios
    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarTodosLosUsuarios());
    }

    // GET /api/admin/usuarios/{id} - Ver detalles de un usuario
    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Ver detalles de usuario", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> verUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.verUsuarioPorId(id));
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
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Nuevos datos del usuario") @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(adminService.editarUsuario(id, usuarioDto));
    }

    // PUT /api/admin/usuarios/{id}/activar - Activar usuario
    @PutMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar usuario", description = "Activa un usuario desactivado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> activarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.activarUsuario(id));
    }

    // PUT /api/admin/usuarios/{id}/desactivar - Desactivar usuario
    @PutMapping("/usuarios/{id}/desactivar")
    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario activo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.desactivarUsuario(id));
    }

    // POST /api/admin/usuarios/{id}/bloquear - Bloquear usuario
    @PostMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.bloquearUsuario(id));
    }

    // POST /api/admin/usuarios/{id}/desbloquear - Desbloquear usuario
    @PostMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<UsuarioResponseDto> desbloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.desbloquearUsuario(id));
    }

    // POST /api/admin/usuarios/{id}/reset-password - Resetear contraseña de usuario
    @PostMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<UsuarioResponseDto> resetearPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blanquearPassword(id));
    }

    // PUT /api/admin/usuarios/{id}/rol - Cambiar el rol de un usuario
    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto cambiarRolDto) {
        return ResponseEntity.ok(adminService.cambiarRolUsuario(id, cambiarRolDto));
    }

    // GET /api/admin/tickets - Listar todos los tickets del sistema
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> listarTickets() {
        return ResponseEntity.ok(adminService.listarTodosLosTickets());
    }

    // POST /api/admin/tickets/{id}/reabrir - Reabrir un ticket cerrado
    @PostMapping("/tickets/{id}/reabrir")
    public ResponseEntity<TicketResponseDto> reabrirTicket(@PathVariable Long id, @RequestParam String comentario) {
        return ResponseEntity.ok(adminService.reabrirTicket(id, comentario));
    }

    // GET /api/admin/usuarios/rol?rol=TECNICO - Listar usuarios por rol
    @GetMapping("/usuarios/rol")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorRol(@RequestParam String rol) {
        return ResponseEntity.ok(adminService.listarUsuariosPorRol(rol));
    }

}
