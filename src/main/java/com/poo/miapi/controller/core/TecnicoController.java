package com.poo.miapi.controller.core;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TecnicoService;

@RestController
@RequestMapping("/api/tecnico")
@Tag(name = "Técnicos", description = "Endpoints para gestión de técnicos y asignación de tickets")
public class TecnicoController {

        private final TecnicoService tecnicoService;

        public TecnicoController(TecnicoService tecnicoService) {
                this.tecnicoService = tecnicoService;
        }

        // GET /api/tecnico/tickets/asignados?userId=...
        @GetMapping("/tickets/asignados")
        @Operation(summary = "Ver tickets asignados", description = "Obtiene todos los tickets asignados a un técnico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de tickets asignados obtenida exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico no encontrado")
        })
        public ResponseEntity<List<TicketResponseDto>> verMisTickets(
                        @Parameter(description = "ID del técnico") @RequestParam int userId) {
                List<TicketResponseDto> tickets = tecnicoService.verTicketsAsignados(userId);
                return ResponseEntity.ok(tickets);
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
                tecnicoService.tomarTicket(idTecnico, ticketId);
                return ResponseEntity.ok("Ticket tomado correctamente");
        }

        // POST /api/tecnico/tickets/{ticketId}/finalizar
        @PostMapping("/tickets/{ticketId}/finalizar")
        @Operation(summary = "Finalizar ticket", description = "Marca un ticket como finalizado por el técnico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket finalizado correctamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico o ticket no encontrado"),
                        @ApiResponse(responseCode = "400", description = "El ticket no está asignado a este técnico")
        })
        public ResponseEntity<String> finalizarTicket(
                        @Parameter(description = "ID del técnico") @RequestParam int idTecnico,
                        @Parameter(description = "ID del ticket") @PathVariable int ticketId) {
                tecnicoService.finalizarTicket(idTecnico, ticketId);
                return ResponseEntity.ok("Estado de ticket actualizado a: Finalizado");
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
                tecnicoService.devolverTicket(idTecnico, ticketId, motivo);
                return ResponseEntity.ok("Ticket devuelto");
        }

        // GET /api/tecnico/listar-todos - Listar todos los técnicos
        @GetMapping("/listar-todos")
        @Operation(summary = "Listar todos los técnicos", description = "Obtiene una lista de todos los técnicos del sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de técnicos obtenida exitosamente")
        })
        public ResponseEntity<List<TecnicoResponseDto>> listarTodos() {
                return ResponseEntity.ok(tecnicoService.listarTodos());
        }

        // GET /api/tecnico/incidentes?tecnicoId=...
        @GetMapping("/incidentes")
        @Operation(summary = "Obtener historial de incidentes", description = "Obtiene el historial de incidentes de un técnico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Historial de incidentes obtenido exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Técnico no encontrado")
        })
        public ResponseEntity<List<IncidenteTecnicoResponseDto>> obtenerHistorialIncidentes(
                        @Parameter(description = "ID del técnico") @RequestParam int tecnicoId) {
                List<IncidenteTecnicoResponseDto> incidentes = tecnicoService.obtenerHistorialIncidentes(tecnicoId);
                return ResponseEntity.ok(incidentes);
        }
}
