package com.poo.miapi.controller.core;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.tecnico.IncidenciasDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.service.core.TecnicoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/tecnico")
@Tag(name = "Técnicos", description = "Endpoints para gestión de técnicos y asignación de tickets")
public class TecnicoController {

        private final TecnicoService tecnicoService;
        private static final Logger logger = LoggerFactory.getLogger(TecnicoController.class);

        public TecnicoController(TecnicoService tecnicoService) {
                this.tecnicoService = tecnicoService;
        }

        // POST /api/tecnico/tickets/{ticketId}/tomar
        @PostMapping("/tickets/{ticketId}/tomar")
        @Operation(summary = "Tomar ticket", description = "Permite a un técnico tomar tickets NO_ATENDIDO o REABIERTO")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket tomado correctamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico o ticket no encontrado"),
                        @ApiResponse(responseCode = "400", description = "El ticket ya está asignado o no está disponible")
        })
        public ResponseEntity<String> tomarTicket(
                        @Parameter(description = "ID del técnico") @RequestParam int idTecnico,
                        @Parameter(description = "ID del ticket") @PathVariable int ticketId) {
                                logger.info("[TecnicoController] POST /tickets/{}/tomar idTecnico: {}", ticketId, idTecnico);
                                try {
                                        tecnicoService.tomarTicket(idTecnico, ticketId);
                                        logger.info("[TecnicoController] Ticket tomado correctamente");
                                        return ResponseEntity.ok("Ticket tomado correctamente");
                                } catch (EntityNotFoundException e) {
                                        logger.error("Técnico o ticket no encontrado", e);
                                        return ResponseEntity.status(404).body("Técnico o ticket no encontrado");
                                } catch (IllegalStateException | IllegalArgumentException e) {
                                        logger.error("Error de negocio al tomar ticket", e);
                                        return ResponseEntity.status(400).body(e.getMessage());
                                } catch (Exception e) {
                                        logger.error("Error inesperado al tomar ticket", e);
                                        return ResponseEntity.status(500).body("Error interno del servidor");
                                }
        }

        // POST /api/tecnico/tickets/{ticketId}/resolver
        @PostMapping("/tickets/{ticketId}/resolver")
        @Operation(summary = "Resolver ticket", description = "Marca un ticket como resuelto por el técnico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket resuelto correctamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico o ticket no encontrado"),
                        @ApiResponse(responseCode = "400", description = "El ticket no está asignado a este técnico")
        })
        public ResponseEntity<String> resolverTicket(
                        @Parameter(description = "ID del técnico") @RequestParam int idTecnico,
                        @Parameter(description = "ID del ticket") @PathVariable int ticketId) {
                                logger.info("[TecnicoController] INICIO resolverTicket - ticketId: {} idTecnico: {}", ticketId, idTecnico);
                                try {
                                        tecnicoService.resolverTicket(idTecnico, ticketId);
                                        logger.info("[TecnicoController] Estado de ticket actualizado a: Resuelto");
                                        return ResponseEntity.ok("Estado de ticket actualizado a: Resuelto");
                                } catch (EntityNotFoundException e) {
                                        logger.error("Ticket no encontrado", e);
                                        return ResponseEntity.status(404).body("Ticket no encontrado");
                                } catch (IllegalStateException | IllegalArgumentException e) {
                                        logger.error("Error de negocio al resolver ticket", e);
                                        return ResponseEntity.status(400).body(e.getMessage());
                                } catch (Exception e) {
                                        logger.error("Error inesperado al resolver ticket", e);
                                        return ResponseEntity.status(500).body("Error interno del servidor");
                                }
        }

        // POST /api/tecnico/tickets/{ticketId}/devolver
        @PostMapping("/tickets/{ticketId}/devolver")
        @Operation(summary = "Devolver ticket", description = "Devuelve un ticket con un motivo específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket devuelto correctamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico o ticket no encontrado"),
                        @ApiResponse(responseCode = "400", description = "El ticket no está asignado a este técnico")
        })
        public ResponseEntity<String> devolverTicket(
                        @Parameter(description = "ID del técnico") @RequestParam int idTecnico,
                        @Parameter(description = "ID del ticket") @PathVariable int ticketId,
                        @Parameter(description = "Motivo de la devolución del ticket") @RequestParam String motivo) {
                                logger.info("[TecnicoController] POST /tickets/{}/devolver idTecnico: {} motivo: {}", ticketId, idTecnico, motivo);
                                try {
                                        tecnicoService.devolverTicket(idTecnico, ticketId, motivo);
                                        logger.info("[TecnicoController] Ticket devuelto");
                                        return ResponseEntity.ok("Ticket devuelto");
                                } catch (EntityNotFoundException e) {
                                        logger.error("Técnico o ticket no encontrado", e);
                                        return ResponseEntity.status(404).body("Técnico o ticket no encontrado");
                                } catch (IllegalStateException | IllegalArgumentException e) {
                                        logger.error("Error de negocio al devolver ticket", e);
                                        return ResponseEntity.status(400).body(e.getMessage());
                                } catch (Exception e) {
                                        logger.error("Error inesperado al devolver ticket", e);
                                        return ResponseEntity.status(500).body("Error interno del servidor");
                                }
        }

        // GET /api/tecnico/incidentes
        @GetMapping("/incidentes")
        @Operation(summary = "Ver incidencias actuales", description = "Devuelve la cantidad de marcas y fallas del técnico autenticado")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Incidencias actuales", content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = IncidenciasDto.class)))
        })
        public ResponseEntity<IncidenciasDto> verIncidentes(@AuthenticationPrincipal Usuario usuarioAutenticado) {
                logger.info("[TecnicoController] GET /incidentes usuario: {}", usuarioAutenticado != null ? usuarioAutenticado.getId() : null);
                if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null || !"TECNICO".equals(usuarioAutenticado.getRol().name())) {
                        return ResponseEntity.status(403).build();
                }
                var tecnico = tecnicoService.buscarPorId(usuarioAutenticado.getId());
                return ResponseEntity.ok(new IncidenciasDto(tecnico.getFallas(), tecnico.getMarcas()));
        }
}
