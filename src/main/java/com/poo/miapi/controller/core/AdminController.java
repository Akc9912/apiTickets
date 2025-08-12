package com.poo.miapi.controller.core;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.model.core.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administradores", description = "Endpoints para gestión administrativa del sistema")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/admin/usuarios/crear
    @PostMapping("/usuarios/crear")
    @Operation(summary = "Crear usuario", description = "Solo Admin y SuperAdmin pueden crear usuarios. Admin no puede crear SuperAdmin.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRequestDto.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado")
        }
    )
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioRequestDto usuarioDto, @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "No autorizado"));
        }
        try {
            UsuarioResponseDto creado = usuarioService.crearUsuarioConValidacion(usuarioDto, usuarioAutenticado);
            return ResponseEntity.status(201).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/admin/usuarios/{id}/editar
    @PutMapping("/usuarios/{id}/editar")
    @Operation(summary = "Editar usuario", description = "Actualiza los datos de un usuario",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a editar", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRequestDto.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario editado correctamente",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
        }
    )
    public ResponseEntity<UsuarioResponseDto> editarUsuario(@PathVariable int id, @RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.editarDatosUsuario(id, usuarioDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // PUT /api/admin/usuarios/{id}/activar
    @PutMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar usuario", description = "Activa o desactiva el usuario (toggle)",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a activar/desactivar", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
        }
    )
    public ResponseEntity<UsuarioResponseDto> activarUsuario(@PathVariable int id) {
        UsuarioResponseDto resp = usuarioService.setUsuarioActivo(id);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/bloquear
    @PutMapping("/usuarios/{id}/bloquear")
    @Operation(summary = "Bloquear usuario", description = "Bloquea o desbloquea el usuario (toggle)",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a bloquear/desbloquear", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
        }
    )
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(@PathVariable int id) {
        UsuarioResponseDto resp = usuarioService.setUsuarioBloqueado(id);
        return ResponseEntity.ok(resp);
    }

    // POST /api/admin/usuarios/{id}/reset-password
    @PostMapping("/usuarios/{id}/reset-password")
    @Operation(summary = "Resetear contraseña", description = "Resetea la contraseña de un usuario a la por defecto",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a resetear contraseña", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña reseteada",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
        }
    )
    public ResponseEntity<UsuarioResponseDto> resetearPassword(@PathVariable int id) {
        UsuarioResponseDto resp = usuarioService.resetearPassword(id);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/rol
    @PutMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol de un usuario existente",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a cambiar rol", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRequestDto.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rol cambiado",
                content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
        }
    )
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(@PathVariable int id, @RequestBody UsuarioRequestDto cambiarRolDto) {
        UsuarioResponseDto resp = usuarioService.cambiarRolUsuario(id, cambiarRolDto);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/usuarios/rol?rol=TECNICO
    @GetMapping("/usuarios/rol")
    @Operation(summary = "Listar usuarios por rol", description = "Obtiene una lista de usuarios filtrados por rol",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "rol", description = "Rol por el que filtrar (ADMIN, SUPER_ADMIN, TECNICO, TRABAJADOR)", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios filtrados",
                content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class))))
        }
    )
    public ResponseEntity<?> listarUsuariosPorRol(@RequestParam String rol) {
        var resp = usuarioService.listarUsuariosPorRol(rol);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/usuarios/listar-todos
    @GetMapping("/usuarios/listar-todos")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios del sistema",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de todos los usuarios",
                content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class))))
        }
    )
    public ResponseEntity<?> listarTodos() {
        var resp = usuarioService.listarTodos();
        return ResponseEntity.ok(resp);
    }
}
