package com.poo.miapi.controller.auditoria;

import com.poo.miapi.dto.auditoria.AuditoriaResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.service.auditoria.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "Auditoría", description = "Endpoints para consultar logs de auditoría del sistema")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/actividad-reciente")
    @Operation(summary = "Obtener actividad reciente", description = "Devuelve la actividad de las últimas 24 horas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> getActividadReciente(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<AuditoriaResponseDto> actividad = auditoriaService.getActividadReciente(24);
        return ResponseEntity.ok(actividad);
    }

    @GetMapping("/por-usuario/{usuarioId}")
    @Operation(summary = "Obtener auditoría por usuario", description = "Devuelve toda la actividad de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditoría obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<AuditoriaResponseDto>> getAuditoriaPorUsuario(
            @PathVariable Integer usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<AuditoriaResponseDto> auditoria = auditoriaService.getAuditoriaPorUsuario(usuarioId);
        return ResponseEntity.ok(auditoria);
    }

    @GetMapping("/por-entidad")
    @Operation(summary = "Obtener auditoría por entidad", description = "Devuelve toda la actividad relacionada con una entidad específica")
    public ResponseEntity<List<AuditoriaResponseDto>> getAuditoriaPorEntidad(
            @RequestParam String entidadTipo,
            @RequestParam Integer entidadId,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<AuditoriaResponseDto> auditoria = auditoriaService.getAuditoriaPorEntidad(entidadTipo, entidadId);
        return ResponseEntity.ok(auditoria);
    }

    @GetMapping("/eventos-seguridad")
    @Operation(summary = "Obtener eventos de seguridad críticos", description = "Devuelve eventos de seguridad con alta severidad")
    public ResponseEntity<List<AuditoriaResponseDto>> getEventosSeguridadCriticos(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<AuditoriaResponseDto> eventos = auditoriaService.getEventosSeguridadCriticos();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/por-categoria")
    @Operation(summary = "Obtener auditoría por categoría", description = "Devuelve eventos filtrados por categoría con paginación")
    public ResponseEntity<Page<AuditoriaResponseDto>> getAuditoriaPorCategoria(
            @RequestParam CategoriaAuditoria categoria,
            Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        Page<AuditoriaResponseDto> auditoria = auditoriaService.getAuditoriaPorCategoria(categoria, pageable);
        return ResponseEntity.ok(auditoria);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de auditoría", description = "Devuelve estadísticas de actividad por usuario")
    public ResponseEntity<List<Object[]>> getEstadisticasAuditoria(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<Object[]> estadisticas = auditoriaService.getEstadisticasPorUsuario();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar en auditoría", description = "Busca eventos por rango de fechas y otros filtros")
    public ResponseEntity<List<AuditoriaResponseDto>> buscarEnAuditoria(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin,
            @RequestParam(required = false) AccionAuditoria accion,
            @RequestParam(required = false) String entidadTipo,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado) {
        if (!puedeVerAuditoria(usuarioAutenticado)) {
            return ResponseEntity.status(403).build();
        }

        List<AuditoriaResponseDto> resultados = auditoriaService.buscarEnAuditoria(
                fechaInicio, fechaFin, accion, entidadTipo);
        return ResponseEntity.ok(resultados);
    }

    private boolean puedeVerAuditoria(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        // Solo admin y superadmin pueden ver auditoría
        return usuario.getRol().canManageUsers();
    }
}
