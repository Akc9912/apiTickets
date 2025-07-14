package com.poo.miapi.controller;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.TrabajadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajador")
public class TrabajadorController {

    @Autowired
    private TrabajadorService trabajadorService;

    // POST /api/trabajador/tickets - Crear un nuevo ticket
    @PostMapping("/tickets")
    public TicketResponseDto crearTicket(@RequestBody TicketRequestDto dto) {
        return trabajadorService.crearTicket(dto);
    }

    // GET /api/trabajador/tickets - Ver todos mis tickets
    @GetMapping("/tickets")
    public List<TicketResponseDto> verMisTickets(@RequestParam int idTrabajador) {
        return trabajadorService.listarTicketsPorTrabajador(idTrabajador);
    }

    // GET /api/trabajador/tickets/activos - Ver mis tickets activos
    @GetMapping("/tickets/activos")
    public List<TicketResponseDto> verMisTicketsActivos(@RequestParam int idTrabajador) {
        return trabajadorService.listarTicketsActivosPorTrabajador(idTrabajador);
    }

    // POST /api/trabajador/tickets/{ticketId}/evaluar - Evaluar la atención
    // recibida en un ticket
    @PostMapping("/tickets/{ticketId}/evaluar")
    public TicketResponseDto evaluarTicket(@PathVariable int ticketId, @RequestBody EvaluarTicketDto dto) {
        return trabajadorService.evaluarTicket(ticketId, dto);
    }
}
