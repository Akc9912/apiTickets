package com.poo.miapi.controller.historial;

import com.poo.miapi.dto.historial.AuditoriaResponseDto;
import com.poo.miapi.service.historial.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Swagger/OpenAPI eliminado

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
// Swagger/OpenAPI eliminado
public class AuditoriaController {

    @Autowired
    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // GET /api/auditoria
    @GetMapping
    public ResponseEntity<List<AuditoriaResponseDto>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    // GET /api/auditoria/usuario
    @GetMapping("/usuario")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorUsuario(@RequestParam String usuario) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuario));
    }

    // GET /api/auditoria/entidad
    @GetMapping("/entidad")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorEntidad(@RequestParam String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }

    // GET /api/auditoria/accion
    @GetMapping("/accion")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorAccion(@RequestParam String accion) {
        return ResponseEntity.ok(auditoriaService.listarPorAccion(accion));
    }

    // GET /api/auditoria/fecha
    @GetMapping("/fecha")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(auditoriaService.listarPorFecha(desde, hasta));
    }
}
