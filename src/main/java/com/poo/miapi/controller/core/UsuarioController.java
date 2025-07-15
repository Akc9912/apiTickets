package com.poo.miapi.controller.core;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios/obtener-datos
    @GetMapping("/obtener-datos")
    public ResponseEntity<UsuarioResponseDto> obtenerDatos(@RequestParam Long userId) {
        UsuarioResponseDto usuario = usuarioService.obtenerDatos(userId);
        return ResponseEntity.ok(usuario);
    }

    // PUT /api/usuarios/editar-datos
    @PutMapping("/editar-datos")
    public ResponseEntity<UsuarioResponseDto> editarDatos(@RequestParam Long userId,
            @RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.editarDatosUsuario(userId, usuarioDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // PUT /api/usuarios/cambiar-password
    @PutMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordDto dto) {
        String mensaje = usuarioService.cambiarPassword(dto);
        return ResponseEntity.ok(mensaje);
    }

    // GET /api/usuarios/notificaciones
    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionResponseDto>> verMisNotificaciones(@RequestParam Long userId) {
        List<NotificacionResponseDto> notificaciones = usuarioService.verMisNotificaciones(userId);
        return ResponseEntity.ok(notificaciones);
    }

    // GET /api/usuarios/mis-tickets
    @GetMapping("/mis-tickets")
    public ResponseEntity<List<TicketResponseDto>> verMisTickets(@RequestParam Long userId) {
        List<TicketResponseDto> tickets = usuarioService.verMisTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    // GET /api/usuarios/estadisticas/totales
    @GetMapping("/estadisticas/totales")
    public ResponseEntity<Long> contarUsuariosTotales() {
        return ResponseEntity.ok(usuarioService.contarUsuariosTotales());
    }

    // GET /api/usuarios/estadisticas/activos
    @GetMapping("/estadisticas/activos")
    public ResponseEntity<Long> contarUsuariosActivos() {
        return ResponseEntity.ok(usuarioService.contarUsuariosActivos());
    }

    // GET /api/usuarios/estadisticas/tecnicos-bloqueados
    @GetMapping("/estadisticas/tecnicos-bloqueados")
    public ResponseEntity<Long> contarTecnicosBloqueados() {
        return ResponseEntity.ok(usuarioService.contarTecnicosBloqueados());
    }

}