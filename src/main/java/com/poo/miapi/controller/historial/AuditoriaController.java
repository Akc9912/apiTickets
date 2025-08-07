package com.poo.miapi.controller.historial;

import com.poo.miapi.dto.historial.AuditoriaResponseDto;
import com.poo.miapi.service.historial.AuditoriaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoría", description = "Endpoints para consulta de logs de auditoría del sistema")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // GET /api/auditoria
    @GetMapping
    @Operation(summary = "Listar todas las auditorías", description = "Obtiene todos los registros de auditoría del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros de auditoría obtenidos exitosamente")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    // GET /api/auditoria/usuario
    @GetMapping("/usuario")
    @Operation(summary = "Listar auditorías por usuario", description = "Obtiene registros de auditoría filtrados por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros filtrados obtenidos exitosamente")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorUsuario(
            @Parameter(description = "Nombre del usuario") @RequestParam String usuario) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuario));
    }

    // GET /api/auditoria/entidad
    @GetMapping("/entidad")
    @Operation(summary = "Listar auditorías por entidad", description = "Obtiene registros de auditoría filtrados por entidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros filtrados obtenidos exitosamente")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorEntidad(
            @Parameter(description = "Nombre de la entidad") @RequestParam String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }

    // GET /api/auditoria/accion
    @GetMapping("/accion")
    @Operation(summary = "Listar auditorías por acción", description = "Obtiene registros de auditoría filtrados por tipo de acción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros filtrados obtenidos exitosamente")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorAccion(
            @Parameter(description = "Tipo de acción") @RequestParam String accion) {
        return ResponseEntity.ok(auditoriaService.listarPorAccion(accion));
    }

    // GET /api/auditoria/fecha
    @GetMapping("/fecha")
    @Operation(summary = "Listar auditorías por rango de fechas", description = "Obtiene registros de auditoría en un rango de fechas específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros filtrados obtenidos exitosamente"),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorFecha(
            @Parameter(description = "Fecha de inicio (formato ISO: YYYY-MM-DDTHH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha de fin (formato ISO: YYYY-MM-DDTHH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(auditoriaService.listarPorFecha(desde, hasta));
    }
}
