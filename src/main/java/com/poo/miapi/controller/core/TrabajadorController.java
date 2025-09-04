package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TrabajadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trabajador")
@Tag(name = "Trabajadores", description = "Endpoints para gestión de trabajadores y creación de tickets")
public class TrabajadorController {

    private final TrabajadorService trabajadorService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TrabajadorController.class);

    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }
    
    // POST /api/trabajador/tickets/{ticketId}/evaluar - Validación final del trabajador
    // después de que el técnico finaliza el ticket (aceptar/rechazar solución)
    @PostMapping("/tickets/{ticketId}/evaluar")
    @Operation(summary = "Evaluar solución de ticket", description = "Validación final del trabajador: acepta la solución (RESUELTO) o la rechaza (REABIERTO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket evaluado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "El ticket no está en estado FINALIZADO o ya fue evaluado")
    })
    public TicketResponseDto evaluarTicket(
            @Parameter(description = "ID del ticket") @PathVariable int ticketId,
            @Parameter(description = "Datos de la evaluación") @RequestBody EvaluarTicketDto dto) {
    logger.info("[TrabajadorController] POST /tickets/{}/evaluar datos: {}", ticketId, dto);
    logger.info("[TrabajadorController] ticketId recibido: {} (tipo: int)", ticketId);
    try {
        TicketResponseDto resp = trabajadorService.evaluarTicket(ticketId, dto);
        logger.info("[TrabajadorController] Respuesta: {}", resp);
        return resp;
    } catch (Exception e) {
        logger.error("[TrabajadorController] Error al evaluar ticketId {}: {}", ticketId, e.getMessage(), e);
        throw e;
    }
    }

    
}
