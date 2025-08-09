package com.poo.miapi.controller.core;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios del sistema")
public class UsuarioController {

        private final UsuarioService usuarioService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UsuarioController.class);

        public UsuarioController(UsuarioService usuarioService) {
                this.usuarioService = usuarioService;
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

        // PUT /api/usuarios/cambiar-password
        @PutMapping("/cambiar-password")
        @Operation(summary = "Cambiar contraseña del usuario", description = "Permite al usuario cambiar su contraseña")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o datos inválidos")
        })
        public ResponseEntity<String> cambiarPassword(
                        @Parameter(description = "Datos para cambiar la contraseña") @RequestBody ChangePasswordDto dto) {
                logger.info("[UsuarioController] PUT /cambiar-password datos: {}", dto);
                String mensaje = usuarioService.cambiarPassword(dto);
                logger.info("[UsuarioController] Respuesta: {}", mensaje);
                return ResponseEntity.ok(mensaje);
        }

        // GET /api/usuarios/notificaciones
        @GetMapping("/notificaciones")
        @Operation(summary = "Ver notificaciones del usuario", description = "Obtiene todas las notificaciones de un usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public ResponseEntity<List<NotificacionResponseDto>> verMisNotificaciones(
                        @Parameter(description = "ID del usuario") @RequestParam int userId) {
                logger.info("[UsuarioController] GET /notificaciones userId: {}", userId);
                List<NotificacionResponseDto> notificaciones = usuarioService.verMisNotificaciones(userId);
                logger.info("[UsuarioController] Respuesta: {}", notificaciones);
                return ResponseEntity.ok(notificaciones);
        }

        // GET /api/usuarios/mis-tickets
        @GetMapping("/mis-tickets")
        @Operation(summary = "Ver tickets del usuario", description = "Obtiene todos los tickets de un usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tickets obtenidos exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public ResponseEntity<List<TicketResponseDto>> verMisTickets(
                        @Parameter(description = "ID del usuario") @RequestParam int userId) {
                logger.info("[UsuarioController] GET /mis-tickets userId: {}", userId);
                List<TicketResponseDto> tickets = usuarioService.verMisTickets(userId);
                logger.info("[UsuarioController] Respuesta: {}", tickets);
                return ResponseEntity.ok(tickets);
        }

        // GET /api/usuarios/estadisticas/totales
        @GetMapping("/estadisticas/totales")
        @Operation(summary = "Contar usuarios totales", description = "Obtiene el total de usuarios registrados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
        })
        public ResponseEntity<Long> contarUsuariosTotales() {
                logger.info("[UsuarioController] GET /estadisticas/totales");
                Long total = usuarioService.contarUsuariosTotales();
                logger.info("[UsuarioController] Respuesta: {}", total);
                return ResponseEntity.ok(total);
        }

        // GET /api/usuarios/estadisticas/activos
        @GetMapping("/estadisticas/activos")
        @Operation(summary = "Contar usuarios activos", description = "Obtiene el total de usuarios activos en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
        })
        public ResponseEntity<Long> contarUsuariosActivos() {
                logger.info("[UsuarioController] GET /estadisticas/activos");
                Long activos = usuarioService.contarUsuariosActivos();
                logger.info("[UsuarioController] Respuesta: {}", activos);
                return ResponseEntity.ok(activos);
        }

        // GET /api/usuarios/estadisticas/tecnicos-bloqueados
        @GetMapping("/estadisticas/tecnicos-bloqueados")
        @Operation(summary = "Contar técnicos bloqueados", description = "Obtiene el total de técnicos bloqueados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
        })
        public ResponseEntity<Long> contarTecnicosBloqueados() {
                logger.info("[UsuarioController] GET /estadisticas/tecnicos-bloqueados");
                Long bloqueados = usuarioService.contarTecnicosBloqueados();
                logger.info("[UsuarioController] Respuesta: {}", bloqueados);
                return ResponseEntity.ok(bloqueados);
        }

}