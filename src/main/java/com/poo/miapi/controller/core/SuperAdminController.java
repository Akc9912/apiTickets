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
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SuperAdminController.class);

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
        logger.info("[SuperAdminController] POST /usuarios datos: {}", usuarioDto);
        UsuarioResponseDto resp = superAdminService.crearUsuario(usuarioDto);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Listar todos los usuarios", description = "Obtener lista completa de usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodosLosUsuarios() {
        logger.info("[SuperAdminController] GET /usuarios");
        List<UsuarioResponseDto> resp = superAdminService.listarTodosLosUsuarios();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Ver usuario por ID", description = "Obtener detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> verUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] GET /usuarios/{}", id);
        UsuarioResponseDto resp = superAdminService.verUsuarioPorId(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
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
        logger.info("[SuperAdminController] PUT /usuarios/{} datos: {}", id, usuarioDto);
        UsuarioResponseDto resp = superAdminService.editarUsuario(id, usuarioDto);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Eliminar usuario", description = "Eliminar permanentemente un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] DELETE /usuarios/{}", id);
        superAdminService.eliminarUsuario(id);
        logger.info("[SuperAdminController] Usuario eliminado permanentemente");
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
        logger.info("[SuperAdminController] PUT /usuarios/{}/activar", id);
        UsuarioResponseDto resp = superAdminService.activarUsuario(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Desactivar usuario", description = "Desactivar un usuario activo del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] PUT /usuarios/{}/desactivar", id);
        UsuarioResponseDto resp = superAdminService.desactivarUsuario(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Bloquear usuario", description = "Bloquear un usuario del sistema por mal comportamiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario bloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] POST /usuarios/{}/bloquear", id);
        UsuarioResponseDto resp = superAdminService.bloquearUsuario(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Desbloquear usuario", description = "Desbloquear un usuario previamente bloqueado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desbloqueado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<UsuarioResponseDto> desbloquearUsuario(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] POST /usuarios/{}/desbloquear", id);
        UsuarioResponseDto resp = superAdminService.desbloquearUsuario(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Resetear contraseña", description = "Resetear la contraseña de un usuario a la contraseña por defecto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña reseteada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<UsuarioResponseDto> resetearPassword(
            @Parameter(description = "ID del usuario") @PathVariable int id) {
        logger.info("[SuperAdminController] POST /usuarios/{}/reset-password", id);
        UsuarioResponseDto resp = superAdminService.resetearPassword(id);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
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
        logger.info("[SuperAdminController] PUT /usuarios/{}/rol datos: {}", id, cambiarRolDto);
        UsuarioResponseDto resp = superAdminService.cambiarRolUsuario(id, cambiarRolDto);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Listar usuarios por rol", description = "Obtener lista de usuarios filtrados por rol específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    @GetMapping("/usuarios/rol")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorRol(
            @Parameter(description = "Rol a filtrar (SUPERADMIN, ADMIN, TECNICO, TRABAJADOR)") @RequestParam String rol) {
        logger.info("[SuperAdminController] GET /usuarios/rol rol: {}", rol);
        List<UsuarioResponseDto> resp = superAdminService.listarUsuariosPorRol(rol);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
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

    // === GESTIÓN DEL SISTEMA ===

    @Operation(summary = "Listar todos los tickets", description = "Obtener lista completa de todos los tickets del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente")
    })
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> listarTodosLosTickets() {
        logger.info("[SuperAdminController] GET /tickets");
        List<TicketResponseDto> resp = superAdminService.listarTodosLosTickets();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
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
        logger.info("[SuperAdminController] POST /tickets/{}/reabrir comentario: {}", id, comentario);
        TicketResponseDto resp = superAdminService.reabrirTicket(id, comentario);
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Eliminar ticket", description = "Eliminar permanentemente un ticket del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<String> eliminarTicket(
            @Parameter(description = "ID del ticket") @PathVariable int id) {
        logger.info("[SuperAdminController] DELETE /tickets/{}", id);
        superAdminService.eliminarTicket(id);
        logger.info("[SuperAdminController] Ticket eliminado permanentemente");
        return ResponseEntity.ok("Ticket eliminado permanentemente");
    }

    // === ESTADÍSTICAS GLOBALES ===

    @Operation(summary = "Estadísticas de usuarios", description = "Obtener estadísticas completas de usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/usuarios")
    public ResponseEntity<?> obtenerEstadisticasUsuarios() {
        logger.info("[SuperAdminController] GET /estadisticas/usuarios");
        Object resp = superAdminService.obtenerEstadisticasUsuarios();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Estadísticas de tickets", description = "Obtener estadísticas completas de tickets del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/tickets")
    public ResponseEntity<?> obtenerEstadisticasTickets() {
        logger.info("[SuperAdminController] GET /estadisticas/tickets");
        Object resp = superAdminService.obtenerEstadisticasTickets();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Estadísticas del sistema", description = "Obtener estadísticas globales del sistema completo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas del sistema obtenidas exitosamente")
    })
    @GetMapping("/estadisticas/sistema")
    public ResponseEntity<?> obtenerEstadisticasSistema() {
        logger.info("[SuperAdminController] GET /estadisticas/sistema");
        Object resp = superAdminService.obtenerEstadisticasSistema();
        logger.info("[SuperAdminController] Respuesta: {}", resp);
        return ResponseEntity.ok(resp);
    }
}
