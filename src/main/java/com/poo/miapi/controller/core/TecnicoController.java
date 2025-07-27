package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TecnicoService;

@RestController
@RequestMapping("/api/tecnico")
public class TecnicoController {

        @Autowired
        private final TecnicoService tecnicoService;

        public TecnicoController(TecnicoService tecnicoService) {
                this.tecnicoService = tecnicoService;
        }

        // GET /api/tecnico/tickets/asignados?userId=...
        @GetMapping("/tickets/asignados")
        public ResponseEntity<List<TicketResponseDto>> verMisTickets(@RequestParam Long userId) {
                List<TicketResponseDto> tickets = tecnicoService.verTicketsAsignados(userId);
                return ResponseEntity.ok(tickets);
        }

        // POST /api/tecnico/tickets/{ticketId}/tomar
        @PostMapping("/tickets/{ticketId}/tomar")
        public ResponseEntity<String> tomarTicket(
                        @RequestParam Long idTecnico,
                        @PathVariable Long ticketId) {
                tecnicoService.tomarTicket(idTecnico, ticketId);
                return ResponseEntity.ok("Ticket tomado correctamente");
        }

        // POST /api/tecnico/tickets/{ticketId}/finalizar
        @PostMapping("/tickets/{ticketId}/finalizar")

        public ResponseEntity<String> finalizarTicket(
                        @RequestParam Long idTecnico,
                        @PathVariable Long ticketId) {
                tecnicoService.finalizarTicket(idTecnico, ticketId);
                return ResponseEntity.ok("Estado de ticket actualizado a: Finalizado");
        }

        // POST /api/tecnico/tickets/{ticketId}/devolver
        @PostMapping("/tickets/{ticketId}/devolver")

        public ResponseEntity<String> devolverTicket(
                        @RequestParam Long idTecnico,
                        @PathVariable Long ticketId,
                        @RequestParam String motivo) {
                tecnicoService.devolverTicket(idTecnico, ticketId, motivo);
                return ResponseEntity.ok("Ticket devuelto");
        }

        // GET /api/tecnico/listar-todos - Listar todos los técnicos
        @GetMapping("/listar-todos")
        public ResponseEntity<List<TecnicoResponseDto>> listarTodos() {
                return ResponseEntity.ok(tecnicoService.listarTodos());
        }

        // GET /api/tecnico/incidentes?tecnicoId=...
        @GetMapping("/incidentes")

        public ResponseEntity<List<IncidenteTecnicoResponseDto>> obtenerHistorialIncidentes(
                        @RequestParam Long tecnicoId) {
                List<IncidenteTecnicoResponseDto> incidentes = tecnicoService.obtenerHistorialIncidentes(tecnicoId);
                return ResponseEntity.ok(incidentes);
        }
}
