package com.poo.miapi.controller.notificacion;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.service.notificacion.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para gestión de notificaciones del sistema")
public class NotificacionController {

    @Autowired
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // GET /api/notificaciones
    // Ver mis notificaciones (requiere userId, puede obtenerse del token o como
    // parámetro)
    @GetMapping
    @Operation(summary = "Ver mis notificaciones", description = "Obtiene todas las notificaciones de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<NotificacionResponseDto>> verMisNotificaciones(
            @Parameter(description = "ID del usuario") @RequestParam Long userId) {
        List<NotificacionResponseDto> notificaciones = notificacionService.obtenerNotificaciones(userId);
        return ResponseEntity.ok(notificaciones);
    }

    // DELETE /api/notificaciones
    // Eliminar todas mis notificaciones
    @DeleteMapping
    @Operation(summary = "Eliminar todas mis notificaciones", description = "Elimina todas las notificaciones de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificaciones eliminadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminarTodasMisNotificaciones(
            @Parameter(description = "ID del usuario") @RequestParam Long userId) {
        notificacionService.eliminarTodasDelUsuario(userId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/notificaciones
    // (Admin) Enviar notificación a usuario específico
    @PostMapping
    @Operation(summary = "Enviar notificación", description = "Envía una notificación a un usuario específico (función administrativa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación enviada exitosamente", content = @Content(schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Mensaje inválido")
    })
    public ResponseEntity<NotificacionResponseDto> enviarNotificacion(
            @Parameter(description = "ID del usuario destinatario") @RequestParam Long idUsuario,
            @Parameter(description = "Mensaje de la notificación") @RequestParam String mensaje) {
        NotificacionResponseDto dto = notificacionService.enviarNotificacion(idUsuario, mensaje);
        return ResponseEntity.ok(dto);
    }

    // GET /api/notificaciones/count
    // Contar notificaciones de un usuario
    @GetMapping("/count")
    @Operation(summary = "Contar notificaciones", description = "Obtiene el número total de notificaciones de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Long> contarNotificaciones(
            @Parameter(description = "ID del usuario") @RequestParam Long userId) {
        long count = notificacionService.contarNotificaciones(userId);
        return ResponseEntity.ok(count);
    }
}
