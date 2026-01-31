package com.poo.miapi.controller.core;

import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.service.core.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios del sistema")
public class UsuarioController {
        
        private final UsuarioService usuarioService;
        private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

        public UsuarioController(UsuarioService usuarioService) {
                this.usuarioService = usuarioService;
        }

        
        // POST /api/usuarios/crear
        @PostMapping("/crear")
        @Operation(summary = "Crear usuario", description = "Solo Admin y SuperAdmin pueden crear usuarios. Admin no puede crear SuperAdmin.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "No autorizado o restricción de rol", content = @Content),
                @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
        })
        public ResponseEntity<?> crearUsuario(
                        @Parameter(description = "Datos del nuevo usuario") @RequestBody UsuarioRequestDto usuarioDto,
                        @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
                                // Solo accesible por admin/superadmin, la validación y lógica se delega al service
                                if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null) {
                                        return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
                                }
                                try {
                                        UsuarioResponseDto creado = usuarioService.crearUsuarioConValidacion(usuarioDto, usuarioAutenticado);
                                        return ResponseEntity.status(201).body(creado);
                                } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
                                }
        }


        // GET /api/usuarios/obtener-datos
        @GetMapping("/obtener-datos")
        @Operation(summary = "Obtener datos del usuario", description = "Obtiene los datos de un usuario específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Datos del usuario obtenidos exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public ResponseEntity<UsuarioResponseDto> obtenerDatos(
                        @Parameter(description = "ID del usuario") @RequestParam int userId) {
                logger.info("[UsuarioController] GET /obtener-datos userId: {}", userId);
                UsuarioResponseDto usuario = usuarioService.obtenerDatos(userId);
                logger.info("[UsuarioController] Respuesta: {}", usuario);
                return ResponseEntity.ok(usuario);
        }

        // PUT /api/usuarios/editar-datos
        @PutMapping("/editar-datos")
        @Operation(summary = "Editar datos del usuario", description = "Actualiza los datos de un usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Datos actualizados exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos")
        })
        public ResponseEntity<UsuarioResponseDto> editarDatos(
                        @Parameter(description = "ID del usuario") @RequestParam int userId,
                        @Parameter(description = "Nuevos datos del usuario") @RequestBody UsuarioRequestDto usuarioDto) {
                logger.info("[UsuarioController] PUT /editar-datos userId: {} datos: {}", userId, usuarioDto);
                UsuarioResponseDto usuarioActualizado = usuarioService.editarDatosUsuario(userId, usuarioDto);
                logger.info("[UsuarioController] Respuesta: {}", usuarioActualizado);
                return ResponseEntity.ok(usuarioActualizado);
        }


}