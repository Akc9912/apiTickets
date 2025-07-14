package com.poo.miapi.controller.core;

import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    // GET /api/usuario/dator traer mis datos de usuario para armar el perfil.
    @GetMapping("/obtener-datos")
    public ResponseEntity<UsuarioResponseDto> obtenerDatos(Long userId) {
        UsuarioResponseDto usuario = usuarioService.obtenerDatos(userId);
        return ResponseEntity.ok(usuario);
    }

    // PUT /api/usuario/perfil Editar mis datos personales.
    @PutMapping("/editar-datos")
    public ResponseEntity<UsuarioResponseDto> editarDatos(@RequestBody Long userId,
            @RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.editarDatosUsuario(userId, usuarioDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // PUT /api/usuario/password Cambiar mi contraseña.

    @PutMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@RequestParam ChangePasswordDto dto) {
        String mensaje = usuarioService.cambiarPassword(dto);
        return ResponseEntity.ok(mensaje);
    }

    // GET /api/usuario/notificaciones Ver mis notificaciones.

    @GetMapping("/notificaciones")
    public ResponseEntity<Optional<Notificacion>> verMisNotificaciones(Long userId) {
        Optional<Notificacion> notificaciones = usuarioService.verMisNotificaciones(userId);
        return ResponseEntity.ok(notificaciones);
    }

}