package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.service.core.TrabajadorService;
import com.poo.miapi.service.notificacion.motor.EventPublisherService;
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
    private final EventPublisherService eventPublisherService;
    private final TicketRepository ticketRepository;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TrabajadorController.class);

    public TrabajadorController(TrabajadorService trabajadorService, EventPublisherService eventPublisherService,
            TicketRepository ticketRepository) {
        this.trabajadorService = trabajadorService;
        this.eventPublisherService = eventPublisherService;
        this.ticketRepository = ticketRepository;
    }

    // POST /api/trabajador/tickets/{ticketId}/evaluar - Validación final del
    // trabajador
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
            // 1. Evaluar ticket (lógica de negocio existente)
            TicketResponseDto resp = trabajadorService.evaluarTicket(ticketId, dto);

            // 2. Publicar evento para notificación automática
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Ticket no encontrado"));
            Usuario creador = ticket.getCreador(); // El trabajador que evalúa
            Usuario tecnico = ticket.getUltimoTecnicoAtendio(); // El técnico que resolvió

            if (tecnico != null) {
                eventPublisherService.publicarTicketEvaluado(ticket, creador, tecnico, dto.isFueResuelto(),
                        dto.isFueResuelto() ? "Evaluado como resuelto" : dto.getMotivoFalla());
            }

            logger.info("[TrabajadorController] Respuesta: {} - Evento publicado", resp);
            return resp;
        } catch (Exception e) {
            logger.error("[TrabajadorController] Error al evaluar ticketId {}: {}", ticketId, e.getMessage(), e);
            throw e;
        }
    }

}
