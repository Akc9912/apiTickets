package com.poo.miapi.controller;

import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> listarTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.buscarPorId(id));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Ticket>> buscarPorEstado(@RequestParam EstadoTicket estado) {
        return ResponseEntity.ok(ticketService.obtenerPorEstado(estado));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Ticket>> ticketsSinAsignar() {
        return ResponseEntity.ok(ticketService.obtenerDisponibles());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Ticket>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(ticketService.buscarPorTitulo(titulo));
    }
}
