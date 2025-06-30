package com.poo.miapi.controller;

import com.poo.miapi.dto.loginRequest;
import com.poo.miapi.dto.loginResponse;
import com.poo.miapi.model.Usuario;
import com.poo.miapi.service.authService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class authController {

    @Autowired
    private authService authService;

    @PostMapping("/login")
    public loginResponse login(@RequestBody loginRequest request) {
        Usuario usuario = authService.login(request.getId(), request.getPassword());

        if (usuario == null) {
            throw new RuntimeException("Credenciales inválidas.");
        }

        return new loginResponse(
                usuario.getNombre(),
                usuario.getTipoUsuario(),
                usuario.getCambiarPass());
    }
}
