package com.poo.miapi.controller.estadistica;

import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.service.estadistica.EstadisticaService;
import com.poo.miapi.dto.estadistica.EstadisticaPeriodoDto;
import com.poo.miapi.model.enums.PeriodoTipo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
@Tag(name = "Estadísticas", description = "Endpoints para obtener estadísticas del sistema")
public class EstadisticaController {

        private final EstadisticaService estadisticaService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EstadisticaController.class);

        public EstadisticaController(EstadisticaService estadisticaService) {
                this.estadisticaService = estadisticaService;
        }

        // GET /api/estadisticas/tickets/total
        @GetMapping("/tickets/total")
        @Operation(summary = "Total de tickets", description = "Obtiene la cantidad total de tickets en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadTotalTickets() {
                logger.info("[EstadisticaController] GET /tickets/total");
                long resp = estadisticaService.cantidadTotalTickets();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/tickets/estado?estado=NO_ATENDIDO
        @GetMapping("/tickets/estado")
        @Operation(summary = "Tickets por estado", description = "Obtiene la cantidad de tickets filtrados por estado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadTicketsPorEstado(
                        @Parameter(description = "Estado del ticket (NO_ATENDIDO, EN_PROCESO, RESUELTO, CERRADO)") @RequestParam EstadoTicket estado) {
                logger.info("[EstadisticaController] GET /tickets/estado estado: {}", estado);
                long resp = estadisticaService.cantidadTicketsPorEstado(estado);
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/usuarios/total
        @GetMapping("/usuarios/total")
        @Operation(summary = "Total de usuarios", description = "Obtiene la cantidad total de usuarios registrados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadTotalUsuarios() {
                logger.info("[EstadisticaController] GET /usuarios/total");
                long resp = estadisticaService.cantidadTotalUsuarios();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/tecnicos/total
        @GetMapping("/tecnicos/total")
        @Operation(summary = "Total de técnicos", description = "Obtiene la cantidad total de técnicos en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadTotalTecnicos() {
                logger.info("[EstadisticaController] GET /tecnicos/total");
                long resp = estadisticaService.cantidadTotalTecnicos();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/trabajadores/total
        @GetMapping("/trabajadores/total")
        @Operation(summary = "Total de trabajadores", description = "Obtiene la cantidad total de trabajadores en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadTotalTrabajadores() {
                logger.info("[EstadisticaController] GET /trabajadores/total");
                long resp = estadisticaService.cantidadTotalTrabajadores();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/incidentes/total
        @GetMapping("/incidentes/total")
        @Operation(summary = "Total de incidentes", description = "Obtiene la cantidad total de incidentes de técnicos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
        })
        public long cantidadIncidentesTecnicos() {
                logger.info("[EstadisticaController] GET /incidentes/total");
                long resp = estadisticaService.cantidadIncidentesTecnicos();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/usuarios/global
        @GetMapping("/usuarios/global")
        @Operation(summary = "Estadísticas globales de usuarios", description = "Obtiene estadísticas completas de usuarios del sistema")
        public Object obtenerEstadisticasUsuarios() {
                logger.info("[EstadisticaController] GET /usuarios/global");
                Object resp = estadisticaService.obtenerEstadisticasUsuarios();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/tickets/global
        @GetMapping("/tickets/global")
        @Operation(summary = "Estadísticas globales de tickets", description = "Obtiene estadísticas completas de tickets del sistema")
        public Object obtenerEstadisticasTickets() {
                logger.info("[EstadisticaController] GET /tickets/global");
                Object resp = estadisticaService.obtenerEstadisticasTickets();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/estadisticas/sistema
        @GetMapping("/sistema")
        @Operation(summary = "Estadísticas globales del sistema", description = "Obtiene estadísticas globales del sistema completo")
        public Object obtenerEstadisticasSistema() {
                logger.info("[EstadisticaController] GET /sistema");
                Object resp = estadisticaService.obtenerEstadisticasSistema();
                logger.info("[EstadisticaController] Respuesta: {}", resp);
                return resp;
        }

        // ========================================
        // NUEVOS ENDPOINTS AVANZADOS
        // ========================================

        @GetMapping("/dashboard")
        @Operation(summary = "Dashboard principal", description = "Obtiene métricas clave para el dashboard principal")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Dashboard generado correctamente", content = @Content(schema = @Schema(implementation = EstadisticaPeriodoDto.class)))
        })
        public ResponseEntity<EstadisticaPeriodoDto> getDashboardPrincipal() {
                logger.info("[EstadisticaController] GET /dashboard");
                EstadisticaPeriodoDto estadisticas = estadisticaService.getEstadisticasMesActual();
                return ResponseEntity.ok(estadisticas);
        }

        @GetMapping("/periodo")
        @Operation(summary = "Estadísticas por período", description = "Obtiene estadísticas filtradas por tipo de período")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente"),
                        @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
        })
        public ResponseEntity<List<EstadisticaPeriodoDto>> getEstadisticasPorPeriodo(
                        @Parameter(description = "Tipo de período (DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL)") @RequestParam PeriodoTipo tipo,
                        @Parameter(description = "Año de consulta", example = "2025") @RequestParam int anio,
                        @Parameter(description = "Mes de consulta (opcional)", example = "12") @RequestParam(required = false) Integer mes) {

                logger.info("[EstadisticaController] GET /periodo tipo: {} anio: {} mes: {}", tipo, anio, mes);
                List<EstadisticaPeriodoDto> estadisticas = estadisticaService.getEstadisticasPorPeriodo(tipo, anio,
                                mes);
                return ResponseEntity.ok(estadisticas);
        }

        @GetMapping("/mes-actual")
        @Operation(summary = "Estadísticas del mes actual", description = "Obtiene las estadísticas del mes en curso")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estadísticas del mes actual", content = @Content(schema = @Schema(implementation = EstadisticaPeriodoDto.class)))
        })
        public ResponseEntity<EstadisticaPeriodoDto> getEstadisticasMesActual() {
                logger.info("[EstadisticaController] GET /mes-actual");
                EstadisticaPeriodoDto estadisticas = estadisticaService.getEstadisticasMesActual();
                return ResponseEntity.ok(estadisticas);
        }

        @GetMapping("/resumen-rapido")
        @Operation(summary = "Resumen rápido", description = "Obtiene un resumen rápido con métricas clave del sistema")
        public ResponseEntity<Map<String, Object>> getResumenRapido() {
                logger.info("[EstadisticaController] GET /resumen-rapido");
                Map<String, Object> resumen = Map.of(
                                "totalTickets", estadisticaService.cantidadTotalTickets(),
                                "totalUsuarios", estadisticaService.cantidadTotalUsuarios(),
                                "totalTecnicos", estadisticaService.cantidadTotalTecnicos(),
                                "totalTrabajadores", estadisticaService.cantidadTotalTrabajadores(),
                                "totalIncidentes", estadisticaService.cantidadIncidentesTecnicos(),
                                "fechaConsulta", LocalDateTime.now());
                return ResponseEntity.ok(resumen);
        }

        // ========================================
        // ENDPOINTS ADMINISTRATIVOS
        // ========================================

        @PostMapping("/recalcular")
        @Operation(summary = "Recalcular estadísticas", description = "Fuerza el recálculo de todas las estadísticas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estadísticas recalculadas correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tienes permisos para esta operación")
        })
        public ResponseEntity<Map<String, Object>> recalcularEstadisticas(Authentication authentication) {
                logger.info("[EstadisticaController] POST /recalcular");

                // Verificar que sea admin o superadmin
                String email = authentication.getName();
                if (!esAdminOSuperAdmin(email)) {
                        return ResponseEntity.status(403)
                                        .body(Map.of("error", "No tienes permisos para esta operación"));
                }

                try {
                        estadisticaService.recalcularEstadisticas();
                        return ResponseEntity.ok(Map.of(
                                        "mensaje", "Estadísticas recalculadas correctamente",
                                        "fecha", LocalDateTime.now()));
                } catch (Exception e) {
                        logger.error("Error recalculando estadísticas", e);
                        return ResponseEntity.status(500).body(Map.of(
                                        "error", "Error al recalcular estadísticas: " + e.getMessage()));
                }
        }

        @PostMapping("/calcular-diarias")
        @Operation(summary = "Calcular estadísticas diarias", description = "Calcula estadísticas para una fecha específica")
        public ResponseEntity<Map<String, Object>> calcularEstadisticasDiarias(
                        @Parameter(description = "Fecha para calcular (formato ISO)", example = "2025-12-28T10:00:00") @RequestParam(required = false) LocalDateTime fecha,
                        Authentication authentication) {

                logger.info("[EstadisticaController] POST /calcular-diarias fecha: {}", fecha);

                // Verificar permisos
                String email = authentication.getName();
                if (!esAdminOSuperAdmin(email)) {
                        return ResponseEntity.status(403)
                                        .body(Map.of("error", "No tienes permisos para esta operación"));
                }

                try {
                        LocalDateTime fechaCalculo = fecha != null ? fecha : LocalDateTime.now();
                        estadisticaService.calcularEstadisticasDiarias(fechaCalculo);

                        return ResponseEntity.ok(Map.of(
                                        "mensaje", "Estadísticas diarias calculadas correctamente",
                                        "fecha", fechaCalculo));
                } catch (Exception e) {
                        logger.error("Error calculando estadísticas diarias", e);
                        return ResponseEntity.status(500).body(Map.of(
                                        "error", "Error al calcular estadísticas diarias: " + e.getMessage()));
                }
        }

        // ========================================
        // MÉTODOS AUXILIARES
        // ========================================

        private boolean esAdminOSuperAdmin(String email) {
                // Implementación simplificada - en producción verificarías roles del usuario
                return email.contains("admin") || email.contains("superadmin");
        }
}
