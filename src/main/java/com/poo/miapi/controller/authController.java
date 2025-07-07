package com.poo.miapi.controller;

import com.poo.miapi.dto.*;
import com.poo.miapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        LoginResponseDto response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@RequestBody @Valid CambioPasswordDto dto) {
        usuarioService.cambiarPassword(dto);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @PostMapping("/reiniciar-password")
    public ResponseEntity<String> reiniciarPassword(@RequestBody @Valid ReinicioPasswordDto dto) {
        usuarioService.reiniciarPassword(dto);
        return ResponseEntity.ok("Contraseña reiniciada correctamente");
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@RequestBody @Valid CrearUsuarioDto dto) {
        UsuarioResponseDto nuevo = usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok(nuevo);
    }
}
