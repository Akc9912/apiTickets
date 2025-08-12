package com.poo.miapi.controller.estadistica;

import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.service.estadistica.EstadisticaService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
