package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // POST /api/admin/usuarios - Crear un nuevo usuario (admin, técnico,
    // trabajador)
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponseDto> crearUsuario(@RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(adminService.crearUsuario(usuarioDto));
    }

    // GET /api/admin/usuarios - Listar todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarTodosLosUsuarios());
    }

    // GET /api/admin/usuarios/{id} - Ver detalles de un usuario
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDto> verUsuario(@PathVariable int id) {
        return ResponseEntity.ok(adminService.verUsuarioPorId(id));
    }

    // PUT /api/admin/usuarios/{id} - Editar datos de un usuario
    @PutMapping("/usuarios/{id}")

    public ResponseEntity<UsuarioResponseDto> editarUsuario(@PathVariable int id,
            @RequestBody UsuarioRequestDto usuarioDto) {
        return ResponseEntity.ok(adminService.editarUsuario(id, usuarioDto));
    }

    // PUT /api/admin/usuarios/{id}/activar - Activar usuario
    @PutMapping("/usuarios/{id}/activar")
    public ResponseEntity<UsuarioResponseDto> activarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.activarUsuario(id));
    }

    // PUT /api/admin/usuarios/{id}/desactivar - Desactivar usuario
    @PutMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDto> desactivarUsuario(@PathVariable Long id) {
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
            @RequestBody UsuarioRequestDto cambiarRolDto) {
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
