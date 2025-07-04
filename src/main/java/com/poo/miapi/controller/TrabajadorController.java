package com.poo.miapi.controller;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.service.TrabajadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    @Autowired
    private TrabajadorService trabajadorService;

    @PostMapping("/{idTrabajador}/crear-ticket")
    public ResponseEntity<Ticket> crearTicket(@PathVariable int idTrabajador,
            @RequestParam String titulo,
            @RequestParam String descripcion) {
        return ResponseEntity.ok(trabajadorService.crearTicket(idTrabajador, titulo, descripcion));
    }

    @GetMapping("/{idTrabajador}/mis-tickets")
    public ResponseEntity<List<Ticket>> verMisTickets(@PathVariable int idTrabajador) {
        return ResponseEntity.ok(trabajadorService.verMisTickets(idTrabajador));
    }

    @GetMapping("/{idTrabajador}/tickets-activos")
    public ResponseEntity<List<Ticket>> verTicketsActivos(@PathVariable int idTrabajador) {
        return ResponseEntity.ok(trabajadorService.verTicketsActivos(idTrabajador));
    }

    @PostMapping("/{idTrabajador}/confirmar")
    public ResponseEntity<?> confirmarResolucion(@PathVariable int idTrabajador,
            @RequestParam int idTicket,
            @RequestParam boolean fueResuelto) {
        trabajadorService.confirmarResolucion(idTrabajador, idTicket, fueResuelto);
        return ResponseEntity.ok("Resolución confirmada");
    }
}
