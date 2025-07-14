package com.poo.miapi.controller;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // GET /api/tickets - Listar todos los tickets
    @GetMapping
    public List<TicketResponseDto> listarTickets() {
        return ticketService.listarTodos();
    }

    // GET /api/tickets/{id} - Ver detalle de un ticket
    @GetMapping("/{id}")
    public TicketResponseDto verTicket(@PathVariable int id) {
        return ticketService.buscarPorId(id);
    }

    // POST /api/tickets - Crear un nuevo ticket
    @PostMapping
    public TicketResponseDto crearTicket(@RequestBody TicketRequestDto dto) {
        return ticketService.crear(dto);
    }

    // PUT /api/tickets/{id} - Editar un ticket existente
    @PutMapping("/{id}")
    public TicketResponseDto editarTicket(@PathVariable int id, @RequestBody TicketRequestDto dto) {
        return ticketService.editar(id, dto);
    }

    // POST /api/tickets/filtrar - Filtrar tickets por estado, usuario, fecha, etc.
    // Puedes crear un DTO específico para filtros si lo necesitas
    @PostMapping("/filtrar")
    public List<TicketResponseDto> filtrarTickets(@RequestBody FiltroTicketsDto filtro) {
        return ticketService.filtrar(filtro);
    }
}
