package com.poo.miapi.controller.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/cambiar-password
    @PostMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@RequestBody @Valid ChangePasswordDto dto) {
        authService.cambiarPassword(dto);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // POST /api/auth/reiniciar-password
    @PostMapping("/reiniciar-password")
    public ResponseEntity<String> reiniciarPassword(@RequestBody @Valid ResetPasswordDto dto) {
        authService.reiniciarPassword(dto);
        return ResponseEntity.ok("Contraseña reiniciada correctamente");
    }
}
