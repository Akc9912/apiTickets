package com.poo.miapi.controller;

import com.poo.miapi.model.Ticket;
import com.poo.miapi.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @PostMapping("/{idTecnico}/tomar/{idTicket}")
    public ResponseEntity<?> tomarTicket(@PathVariable int idTecnico, @PathVariable int idTicket) {
        tecnicoService.tomarTicket(idTecnico, idTicket);
        return ResponseEntity.ok("Ticket tomado exitosamente");
    }

    @PostMapping("/{idTecnico}/resolver/{idTicket}")
    public ResponseEntity<?> resolverTicket(@PathVariable int idTecnico, @PathVariable int idTicket) {
        tecnicoService.resolverTicket(idTecnico, idTicket);
        return ResponseEntity.ok("Ticket marcado como resuelto");
    }

    @PostMapping("/{idTecnico}/devolver/{idTicket}")
    public ResponseEntity<?> devolverTicket(@PathVariable int idTecnico, @PathVariable int idTicket) {
        tecnicoService.devolverTicket(idTecnico, idTicket);
        return ResponseEntity.ok("Ticket devuelto");
    }

    @GetMapping("/{idTecnico}/tickets")
    public ResponseEntity<List<Ticket>> verTicketsAsignados(@PathVariable int idTecnico) {
        return ResponseEntity.ok(tecnicoService.verTicketsAsignados(idTecnico));
    }
}
