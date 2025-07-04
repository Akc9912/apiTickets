package com.poo.miapi.controller;

import com.poo.miapi.dto.LoginRequest;
import com.poo.miapi.dto.LoginResponse;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Login simple (sin JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.login(request.getId(), request.getPassword());
        if (usuario == null) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        return ResponseEntity.ok(new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getTipoUsuario()));
    }

    // Cambio de contraseña
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody LoginRequest request) {
        try {
            usuarioService.cambiarPassword(request.getId(), request.getPassword(), request.getNewPassword());
            return ResponseEntity.ok("Contraseña cambiada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
