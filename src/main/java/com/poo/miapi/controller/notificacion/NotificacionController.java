package com.poo.miapi.controller.notificacion;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.service.notificacion.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
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
    public ResponseEntity<List<NotificacionResponseDto>> verMisNotificaciones(@RequestParam Long userId) {
        List<NotificacionResponseDto> notificaciones = notificacionService.obtenerNotificaciones(userId);
        return ResponseEntity.ok(notificaciones);
    }

    // DELETE /api/notificaciones
    // Eliminar todas mis notificaciones
    @DeleteMapping
    public ResponseEntity<Void> eliminarTodasMisNotificaciones(@RequestParam Long userId) {
        notificacionService.eliminarTodasDelUsuario(userId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/notificaciones
    // (Admin) Enviar notificación a usuario específico
    @PostMapping
    public ResponseEntity<NotificacionResponseDto> enviarNotificacion(
            @RequestParam Long idUsuario,
            @RequestParam String mensaje) {
        NotificacionResponseDto dto = notificacionService.enviarNotificacion(idUsuario, mensaje);
        return ResponseEntity.ok(dto);
    }

    // GET /api/notificaciones/count
    // Contar notificaciones de un usuario
    @GetMapping("/count")
    public ResponseEntity<Long> contarNotificaciones(@RequestParam Long userId) {
        long count = notificacionService.contarNotificaciones(userId);
        return ResponseEntity.ok(count);
    }
}
