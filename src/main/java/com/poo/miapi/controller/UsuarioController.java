package com.poo.miapi.controller;

import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.core.Admin;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequestMapping("/api/usuarios")
public class UsuarioController {

    /*
     * GET /api/usuario/perfil
     * Descripción: Ver mi perfil.
     * PUT /api/usuario/perfil
     * Descripción: Editar mis datos personales.
     * PUT /api/usuario/password
     * Descripción: Cambiar mi contraseña.
     * GET /api/usuario/tickets
     * Descripción: Ver mis tickets (como trabajador o técnico).
     * GET /api/usuario/notificaciones
     * Descripción: Ver mis notificaciones.
     */
}