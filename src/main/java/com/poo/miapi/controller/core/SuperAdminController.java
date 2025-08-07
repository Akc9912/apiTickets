package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
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

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    // === GESTIÓN DE USUARIOS ===

    @Operation(summary = "Crear usuario", description = "Crear cualquier tipo de usuario en el sistema (SuperAdmin, Admin, Técnico, Trabajador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de SuperAdmin")
    })
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponseDto> crearUsuario(
            @Parameter(description = "Datos del nuevo usuario") @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(superAdminService.crearUsuario(usuarioDto));
    }

    @Operation(summary = "Listar todos los usuarios", description = "Obtener lista completa de usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodosLosUsuarios() {
        return ResponseEntity.ok(superAdminService.listarTodosLosUsuarios());
    }

    @Operation(summary = "Ver usuario por ID", description = "Obtener detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> verUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.verUsuarioPorId(id));
    }

    @Operation(summary = "Editar usuario", description = "Actualizar datos de cualquier usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> editarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id,
            @Parameter(description = "Nuevos datos del usuario") @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(superAdminService.editarUsuario(id, usuarioDto));
    }

    @Operation(summary = "Eliminar usuario", description = "Eliminar permanentemente un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        superAdminService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado permanentemente");
    }

    @Operation(summary = "Activar usuario", description = "Activar un usuario desactivado del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/{id}/activar")
    public ResponseEntity<UsuarioResponseDto> activarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.activarUsuario(id));
    }

    @Operation(summary = "Desactivar usuario", description = "Desactivar un usuario activo del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.desactivarUsuario(id));
    }

    @Operation(summary = "Bloquear usuario", description = "Bloquear un usuario del sistema por mal comportamiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario bloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.bloquearUsuario(id));
    }

    @Operation(summary = "Desbloquear usuario", description = "Desbloquear un usuario previamente bloqueado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desbloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<UsuarioResponseDto> desbloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.desbloquearUsuario(id));
    }

    @Operation(summary = "Resetear contraseña", description = "Resetear la contraseña de un usuario a la contraseña por defecto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña reseteada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<UsuarioResponseDto> resetearPassword(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        return ResponseEntity.ok(superAdminService.resetearPassword(id));
    }

    @Operation(summary = "Cambiar rol de usuario", description = "Cambiar el rol de cualquier usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id,
            @Parameter(description = "Nuevo rol del usuario") @Valid @RequestBody UsuarioRequestDto cambiarRolDto) {
        return ResponseEntity.ok(superAdminService.cambiarRolUsuario(id, cambiarRolDto));
    }

    @Operation(summary = "Listar usuarios por rol", description = "Obtener lista de usuarios filtrados por rol específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    @GetMapping("/usuarios/rol")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorRol(
            @Parameter(description = "Rol a filtrar (SUPERADMIN, ADMIN, TECNICO, TRABAJADOR)") @RequestParam String rol) {
        return ResponseEntity.ok(superAdminService.listarUsuariosPorRol(rol));
    }

    // === GESTIÓN DE ADMINISTRADORES ===

    @Operation(summary = "Listar administradores", description = "Obtener lista de todos los administradores del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida exitosamente")
    })
    @GetMapping("/admins")
    public ResponseEntity<List<UsuarioResponseDto>> listarAdministradores() {
        return ResponseEntity.ok(superAdminService.listarAdministradores());
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
        return ResponseEntity.ok(superAdminService.promoverAAdmin(id));
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
        return ResponseEntity.ok(superAdminService.degradarAdmin(id));
    }

    // === GESTIÓN DEL SISTEMA ===

    @Operation(summary = "Listar todos los tickets", description = "Obtener lista completa de todos los tickets del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente")
    })
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> listarTodosLosTickets() {
        return ResponseEntity.ok(superAdminService.listarTodosLosTickets());
    }

    @Operation(summary = "Reabrir ticket", description = "Reabrir cualquier ticket del sistema con comentario explicativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket reabierto exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "El ticket no puede ser reabierto")
    })
    @PostMapping("/tickets/{id}/reabrir")
    public ResponseEntity<TicketResponseDto> reabrirTicket(
            @Parameter(description = "ID del ticket") @PathVariable int id,
            @Parameter(description = "Comentario sobre la reapertura") @RequestParam String comentario) {
        return ResponseEntity.ok(superAdminService.reabrirTicket(id, comentario));
    }

    @Operation(summary = "Eliminar ticket", description = "Eliminar permanentemente un ticket del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<String> eliminarTicket(
            @Parameter(description = "ID del ticket") @PathVariable int id) {
        superAdminService.eliminarTicket(id);
        return ResponseEntity.ok("Ticket eliminado permanentemente");
    }

    // === ESTADÍSTICAS GLOBALES ===

    @Operation(summary = "Estadísticas de usuarios", description = "Obtener estadísticas completas de usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/usuarios")
    public ResponseEntity<?> obtenerEstadisticasUsuarios() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasUsuarios());
    }

    @Operation(summary = "Estadísticas de tickets", description = "Obtener estadísticas completas de tickets del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/tickets")
    public ResponseEntity<?> obtenerEstadisticasTickets() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasTickets());
    }

    @Operation(summary = "Estadísticas del sistema", description = "Obtener estadísticas globales del sistema completo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas del sistema obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/sistema")
    public ResponseEntity<?> obtenerEstadisticasSistema() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasSistema());
    }
}
