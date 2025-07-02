package com.poo.miapi.controller;

import com.poo.miapi.dto.LoginRequest;
import com.poo.miapi.dto.LoginResponse;
import com.poo.miapi.dto.ChangePasswordRequest;
import com.poo.miapi.model.Usuario;
import com.poo.miapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Usuario usuario = authService.login(request.getId(), request.getPassword());
        if (usuario == null) {
            throw new RuntimeException("Credenciales inválidas.");
        }
        return new LoginResponse(usuario.getNombre(), usuario.getTipoUsuario(), usuario.getCambiarPass());
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.cambiarPassword(request.getId(), request.getActual(), request.getNueva());
            return ResponseEntity.ok("Contraseña cambiada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
