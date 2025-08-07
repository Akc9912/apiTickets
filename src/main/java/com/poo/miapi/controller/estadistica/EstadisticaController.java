package com.poo.miapi.controller.estadistica;

import com.poo.miapi.model.core.EstadoTicket;
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
        return estadisticaService.cantidadTotalTickets();
    }

    // GET /api/estadisticas/tickets/estado?estado=NO_ATENDIDO
    @GetMapping("/tickets/estado")
    @Operation(summary = "Tickets por estado", description = "Obtiene la cantidad de tickets filtrados por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
    })
    public long cantidadTicketsPorEstado(
            @Parameter(description = "Estado del ticket (NO_ATENDIDO, EN_PROCESO, RESUELTO, CERRADO)") @RequestParam EstadoTicket estado) {
        return estadisticaService.cantidadTicketsPorEstado(estado);
    }

    // GET /api/estadisticas/usuarios/total
    @GetMapping("/usuarios/total")
    @Operation(summary = "Total de usuarios", description = "Obtiene la cantidad total de usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
    })
    public long cantidadTotalUsuarios() {
        return estadisticaService.cantidadTotalUsuarios();
    }

    // GET /api/estadisticas/tecnicos/total
    @GetMapping("/tecnicos/total")
    @Operation(summary = "Total de técnicos", description = "Obtiene la cantidad total de técnicos en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
    })
    public long cantidadTotalTecnicos() {
        return estadisticaService.cantidadTotalTecnicos();
    }

    // GET /api/estadisticas/trabajadores/total
    @GetMapping("/trabajadores/total")
    @Operation(summary = "Total de trabajadores", description = "Obtiene la cantidad total de trabajadores en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
    })
    public long cantidadTotalTrabajadores() {
        return estadisticaService.cantidadTotalTrabajadores();
    }

    // GET /api/estadisticas/incidentes/total
    @GetMapping("/incidentes/total")
    @Operation(summary = "Total de incidentes", description = "Obtiene la cantidad total de incidentes de técnicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente")
    })
    public long cantidadIncidentesTecnicos() {
        return estadisticaService.cantidadIncidentesTecnicos();
    }
}
