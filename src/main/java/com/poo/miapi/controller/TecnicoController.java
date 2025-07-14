package com.poo.miapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.service.TecnicoService;

@RestController
@RequestMapping("/api/tecnico")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    // GET /api/tecnico/tickets/asignados
    @GetMapping("/tickets/asignados")
    public ResponseEntity<List<?>> verMisTickets(@RequestParam int userId) {
        List<?> tickets = tecnicoService.verTicketsAsignados(userId);
        return ResponseEntity.ok(tickets);
    }

    // POST /api/tecnico/tickets/{ticketId}/tomar
    @PostMapping("/tickets/{ticketId}/tomar")
    public ResponseEntity<String> tomarTicket(@RequestParam int idTecnico, @PathVariable int ticketId) {
        tecnicoService.tomarTicket(idTecnico, ticketId);
        return ResponseEntity.ok("Ticket tomado correctamente");
    }

    // POST /api/tecnico/tickets/{ticketId}/resolver
    @PostMapping("/tickets/{ticketId}/finalizar")
    public ResponseEntity<String> finalizarTicket(@RequestParam int idTecnico, @PathVariable int ticketId) {
        tecnicoService.finalizarTicket(idTecnico, ticketId);
        return ResponseEntity.ok("Estado de ticket actualizado a: Finalizado");
    }

    // POST /api/tecnico/tickets/{ticketId}/devolver
    @PostMapping("/tickets/{ticketId}/devolver")
    public ResponseEntity<String> devolverTicket(@RequestParam int idTecnico, @PathVariable int ticketId,
            @RequestParam String motivo) {
        tecnicoService.devolverTicket(idTecnico, ticketId, motivo);
        return ResponseEntity.ok("Ticket devuelto");
    }
}
