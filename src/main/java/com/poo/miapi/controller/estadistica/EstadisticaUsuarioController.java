package com.poo.miapi.controller.estadistica;

import com.poo.miapi.dto.estadistica.EstadisticaUsuarioDto;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.service.estadistica.EstadisticaUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para estadísticas específicas de usuarios
 * Proporciona endpoints para métricas individuales y agregadas de usuarios
 */
@RestController
@RequestMapping("/api/estadisticas/usuarios")
@Tag(name = "Estadísticas de Usuarios", description = "API para consulta de estadísticas por usuario")
@CrossOrigin(origins = "*")
public class EstadisticaUsuarioController {

    private final EstadisticaUsuarioService estadisticaUsuarioService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(EstadisticaUsuarioController.class);

    public EstadisticaUsuarioController(EstadisticaUsuarioService estadisticaUsuarioService) {
        this.estadisticaUsuarioService = estadisticaUsuarioService;
    }

    // ========================================
    // ESTADÍSTICAS INDIVIDUALES DE USUARIO
    // ========================================

    @GetMapping("/mi-actividad")
    @Operation(summary = "Obtener mis estadísticas personales", description = "Estadísticas de actividad del usuario autenticado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'TECNICO', 'TRABAJADOR')")
    public ResponseEntity<List<EstadisticaUsuarioDto>> obtenerMisEstadisticas(
            Authentication authentication,
            @Parameter(description = "Tipo de período") @RequestParam(defaultValue = "MENSUAL") PeriodoTipo periodo,
            @Parameter(description = "Año de consulta") @RequestParam(required = false) Integer anio,
            @Parameter(description = "Mes de consulta") @RequestParam(required = false) Integer mes) {

        try {
            String email = authentication.getName();
            logger.info("[EstadisticaUsuarioController] GET /mi-actividad - usuario: {}", email);

            List<EstadisticaUsuarioDto> estadisticas = estadisticaUsuarioService.obtenerEstadisticasPorEmail(email,
                    periodo, anio, mes);

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas personales", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Estadísticas de un usuario específico", description = "Métricas detalladas de un usuario por su ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<EstadisticaUsuarioDto>> obtenerEstadisticasUsuario(
            @PathVariable int usuarioId,
            @Parameter(description = "Tipo de período") @RequestParam(defaultValue = "MENSUAL") PeriodoTipo periodo,
            @Parameter(description = "Año de consulta") @RequestParam(required = false) Integer anio,
            @Parameter(description = "Mes de consulta") @RequestParam(required = false) Integer mes) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /usuario/{} - período: {}", usuarioId, periodo);

            List<EstadisticaUsuarioDto> estadisticas = estadisticaUsuarioService
                    .obtenerEstadisticasPorUsuario(usuarioId, periodo, anio, mes);

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas del usuario {}", usuarioId, e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // ========================================
    // ESTADÍSTICAS AGREGADAS POR TIPO DE USUARIO
    // ========================================

    @GetMapping("/por-rol")
    @Operation(summary = "Estadísticas agregadas por rol", description = "Métricas agrupadas por tipo de usuario (Admin, Técnico, etc.)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<EstadisticaUsuarioDto>> obtenerEstadisticasPorRol(
            @Parameter(description = "Tipo de usuario a filtrar") @RequestParam Rol rol,
            @Parameter(description = "Tipo de período") @RequestParam(defaultValue = "MENSUAL") PeriodoTipo periodo,
            @Parameter(description = "Año de consulta") @RequestParam(required = false) Integer anio,
            @Parameter(description = "Mes de consulta") @RequestParam(required = false) Integer mes) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /por-rol - rol: {}, período: {}", rol, periodo);

            List<EstadisticaUsuarioDto> estadisticas = estadisticaUsuarioService.obtenerEstadisticasPorRol(rol, periodo,
                    anio, mes);

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas por rol {}", rol, e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    @GetMapping("/ranking/mas-activos")
    @Operation(summary = "Ranking de usuarios más activos", description = "Top usuarios ordenados por nivel de actividad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Map<String, Object>>> obtenerUsuariosMasActivos(
            @Parameter(description = "Límite de resultados") @RequestParam(defaultValue = "10") int limite,
            @Parameter(description = "Filtrar por rol específico") @RequestParam(required = false) Rol rol) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /ranking/mas-activos - limite: {}", limite);

            List<Map<String, Object>> ranking = estadisticaUsuarioService.obtenerUsuariosMasActivos(limite, rol);

            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            logger.error("Error obteniendo usuarios más activos", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // ========================================
    // MÉTRICAS DE SESIÓN Y CONECTIVIDAD
    // ========================================

    @GetMapping("/sesiones")
    @Operation(summary = "Estadísticas de sesiones de usuarios", description = "Métricas de tiempo conectado y frecuencia de uso")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasSesiones(
            @Parameter(description = "Filtrar por rol específico") @RequestParam(required = false) Rol rol,
            @Parameter(description = "Días hacia atrás para el análisis") @RequestParam(defaultValue = "30") int dias) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /sesiones - rol: {}, días: {}", rol, dias);

            Map<String, Object> estadisticasSesiones = estadisticaUsuarioService.obtenerEstadisticasSesiones(rol, dias);

            return ResponseEntity.ok(estadisticasSesiones);
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas de sesiones", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al obtener estadísticas de sesiones"));
        }
    }

    @GetMapping("/productividad")
    @Operation(summary = "Métricas de productividad por usuario", description = "Análisis de eficiencia y rendimiento individual")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Map<String, Object>>> obtenerMetricasProductividad(
            @Parameter(description = "Filtrar por rol específico") @RequestParam(required = false) Rol rol,
            @Parameter(description = "Período de análisis en meses") @RequestParam(defaultValue = "3") int meses) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /productividad - rol: {}, meses: {}", rol, meses);

            List<Map<String, Object>> metricas = estadisticaUsuarioService.obtenerMetricasProductividad(rol, meses);

            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            logger.error("Error obteniendo métricas de productividad", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // ========================================
    // COMPARATIVAS Y BENCHMARKS
    // ========================================

    @GetMapping("/comparativa-equipos")
    @Operation(summary = "Comparativa entre equipos/roles", description = "Análisis comparativo de rendimiento entre diferentes roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerComparativaEquipos(
            @Parameter(description = "Tipo de período para la comparación") @RequestParam(defaultValue = "MENSUAL") PeriodoTipo periodo,
            @Parameter(description = "Año de consulta") @RequestParam(required = false) Integer anio,
            @Parameter(description = "Mes de consulta") @RequestParam(required = false) Integer mes) {

        try {
            logger.info("[EstadisticaUsuarioController] GET /comparativa-equipos - período: {}", periodo);

            Map<String, Object> comparativa = estadisticaUsuarioService.obtenerComparativaEquipos(periodo, anio, mes);

            return ResponseEntity.ok(comparativa);
        } catch (Exception e) {
            logger.error("Error obteniendo comparativa de equipos", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al generar comparativa de equipos"));
        }
    }

    @GetMapping("/resumen-general")
    @Operation(summary = "Resumen general de todos los usuarios", description = "Vista global de la actividad de todos los usuarios del sistema")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        try {
            logger.info("[EstadisticaUsuarioController] GET /resumen-general");

            Map<String, Object> resumen = estadisticaUsuarioService.obtenerResumenGeneral();
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error obteniendo resumen general de usuarios", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al obtener resumen general"));
        }
    }
}
