package com.poo.miapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.dto.CrearUsuarioDto;
import com.poo.miapi.dto.EditarUsuarioDto;
import com.poo.miapi.dto.CambiarRolDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // POST /api/admin/usuarios Crear un nuevo usuario (admin,técnico,trabajador)

    @PostMapping("/usuarios")
    public String crearUsuario(@Valid @RequestBody CrearUsuarioDto usuarioDto) {
        return adminService.crearUsuario(usuarioDto);
    }

    // GET /api/admin/usuarios Listar todos los usuarios.

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return adminService.listarTodosLosUsuarios();
    }

    // GET /api/admin/usuarios/{id} Ver detalles de un usuario.

    @GetMapping("/usuarios/{id}")
    public Usuario verUsuario(@PathVariable Long id) {
        return adminService.verUsuarioPorId(id);
    }

    // PUT /api/admin/usuarios/{id} Editar datos de un usuario.

    @PutMapping("/usuarios/{id}")
    public String editarUsuario(@PathVariable Long id, @Valid @RequestBody EditarUsuarioDto usuarioDto) {
        return adminService.editarUsuario(id, usuarioDto);
    }

    // PUT /api/admin/usuarios/{id}/activar Activar usuario.

    @PutMapping("/usuarios/{id}/activar")
    public String activarUsuario(@PathVariable Long id) {
        return adminService.activarUsuario(id);
    }

    // PUT /api/admin/usuarios/{id}/desactivar Desactivar usuario.

    @PutMapping("/usuarios/{id}/desactivar")
    public String desactivarUsuario(@PathVariable Long id) {
        return adminService.desactivarUsuario(id);
    }

    // POST /api/admin/usuarios/{id}/bloquear Bloquear usuario.

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable Long id) {
        return adminService.bloquearUsuario(id);
    }

    // POST /api/admin/usuarios/{id}/desbloquear Desbloquear usuario.

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable int id) {
        return adminService.desbloquearUsuario(id);
    }

    // POST /api/admin/usuarios/{id}/reset-password Resetear contraseña de usuario.

    @PostMapping("/usuarios/{id}/reset-password")
    public String resetearPassword(@PathVariable Long id) {
        return adminService.blanquearPassword(id);
    }

    // PUT /api/admin/usuarios/{id}/rol Cambiar el rol de un usuario.

    @PutMapping("/usuarios/{id}/rol")
    public String cambiarRolUsuario(@PathVariable int id, @Valid @RequestBody EditarUsuarioDto cambiarRolDto) {
        return adminService.cambiarRolUsuario(id, cambiarRolDto);
    }

    // GET /api/admin/tickets Listar todos los tickets del sistema.

    @GetMapping("/tickets")
    public List<?> listarTickets() {
        return adminService.listarTodosLosTickets();
    }

    // POST /api/admin/tickets/{id}/reabrir Reabrir un ticket cerrado.

    @PostMapping("/tickets/{id}/reabrir")
    public String reabrirTicket(@PathVariable Long id) {
        return adminService.reabrirTicket(id);
    }

}
