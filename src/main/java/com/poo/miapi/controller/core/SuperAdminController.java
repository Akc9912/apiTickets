package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.SuperAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {
    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    // === GESTIÓN DE USUARIOS ===

    // POST /api/superadmin/usuarios - Crear cualquier tipo de usuario
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponseDto> crearUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(superAdminService.crearUsuario(usuarioDto));
    }

    // GET /api/superadmin/usuarios - Listar todos los usuarios del sistema
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodosLosUsuarios() {
        return ResponseEntity.ok(superAdminService.listarTodosLosUsuarios());
    }

    // GET /api/superadmin/usuarios/{id} - Ver detalles de cualquier usuario
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> verUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.verUsuarioPorId(id));
    }

    // PUT /api/superadmin/usuarios/{id} - Editar cualquier usuario
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> editarUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(superAdminService.editarUsuario(id, usuarioDto));
    }

    // DELETE /api/superadmin/usuarios/{id} - Eliminar permanentemente un usuario
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        superAdminService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado permanentemente");
    }

    // PUT /api/superadmin/usuarios/{id}/activar - Activar usuario
    @PutMapping("/usuarios/{id}/activar")
    public ResponseEntity<UsuarioResponseDto> activarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.activarUsuario(id));
    }

    // PUT /api/superadmin/usuarios/{id}/desactivar - Desactivar usuario
    @PutMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.desactivarUsuario(id));
    }

    // POST /api/superadmin/usuarios/{id}/bloquear - Bloquear usuario
    @PostMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.bloquearUsuario(id));
    }

    // POST /api/superadmin/usuarios/{id}/desbloquear - Desbloquear usuario
    @PostMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<UsuarioResponseDto> desbloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.desbloquearUsuario(id));
    }

    // POST /api/superadmin/usuarios/{id}/reset-password - Resetear contraseña
    @PostMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<UsuarioResponseDto> resetearPassword(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.resetearPassword(id));
    }

    // PUT /api/superadmin/usuarios/{id}/rol - Cambiar rol de usuario
    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto cambiarRolDto) {
        return ResponseEntity.ok(superAdminService.cambiarRolUsuario(id, cambiarRolDto));
    }

    // GET /api/superadmin/usuarios/rol?rol=ADMIN - Listar usuarios por rol
    @GetMapping("/usuarios/rol")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorRol(@RequestParam String rol) {
        return ResponseEntity.ok(superAdminService.listarUsuariosPorRol(rol));
    }

    // === GESTIÓN DE ADMINISTRADORES ===

    // GET /api/superadmin/admins - Listar todos los administradores
    @GetMapping("/admins")
    public ResponseEntity<List<UsuarioResponseDto>> listarAdministradores() {
        return ResponseEntity.ok(superAdminService.listarAdministradores());
    }

    // POST /api/superadmin/admins/{id}/promover - Promover usuario a Admin
    @PostMapping("/admins/{id}/promover")
    public ResponseEntity<UsuarioResponseDto> promoverAAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.promoverAAdmin(id));
    }

    // POST /api/superadmin/admins/{id}/degradar - Degradar Admin a Trabajador
    @PostMapping("/admins/{id}/degradar")
    public ResponseEntity<UsuarioResponseDto> degradarAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(superAdminService.degradarAdmin(id));
    }

    // === GESTIÓN DEL SISTEMA ===

    // GET /api/superadmin/tickets - Ver todos los tickets del sistema
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> listarTodosLosTickets() {
        return ResponseEntity.ok(superAdminService.listarTodosLosTickets());
    }

    // POST /api/superadmin/tickets/{id}/reabrir - Reabrir cualquier ticket
    @PostMapping("/tickets/{id}/reabrir")
    public ResponseEntity<TicketResponseDto> reabrirTicket(@PathVariable Long id, @RequestParam String comentario) {
        return ResponseEntity.ok(superAdminService.reabrirTicket(id, comentario));
    }

    // DELETE /api/superadmin/tickets/{id} - Eliminar ticket permanentemente
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<String> eliminarTicket(@PathVariable Long id) {
        superAdminService.eliminarTicket(id);
        return ResponseEntity.ok("Ticket eliminado permanentemente");
    }

    // === ESTADÍSTICAS GLOBALES ===

    // GET /api/superadmin/estadisticas/usuarios
    @GetMapping("/estadisticas/usuarios")
    public ResponseEntity<?> obtenerEstadisticasUsuarios() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasUsuarios());
    }

    // GET /api/superadmin/estadisticas/tickets
    @GetMapping("/estadisticas/tickets")
    public ResponseEntity<?> obtenerEstadisticasTickets() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasTickets());
    }

    // GET /api/superadmin/estadisticas/sistema
    @GetMapping("/estadisticas/sistema")
    public ResponseEntity<?> obtenerEstadisticasSistema() {
        return ResponseEntity.ok(superAdminService.obtenerEstadisticasSistema());
    }
}
