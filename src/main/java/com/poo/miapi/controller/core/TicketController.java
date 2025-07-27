package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

        @Autowired
        private final TicketService ticketService;

        public TicketController(TicketService ticketService) {
                this.ticketService = ticketService;
        }

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
                return ticketService.crearTicket(dto);
        }

        // PUT /api/tickets/{id}/estado - Actualizar estado de un ticket
        @PutMapping("/{id}/estado")

        public TicketResponseDto actualizarEstado(
                        @PathVariable int id,
                        @RequestParam String estado) {
                return ticketService.actualizarEstado(id, com.poo.miapi.model.core.EstadoTicket.valueOf(estado));
        }

        // GET /api/tickets/estado?estado=NO_ATENDIDO - Listar tickets por estado
        @GetMapping("/estado")

        public List<TicketResponseDto> listarPorEstado(@RequestParam String estado) {
                return ticketService.listarPorEstado(com.poo.miapi.model.core.EstadoTicket.valueOf(estado));
        }

        // GET /api/tickets/creador?userId=... - Listar tickets por creador
        @GetMapping("/creador")
        public List<TicketResponseDto> listarPorCreador(@RequestParam Long userId) {
                return ticketService.listarPorCreador(userId);
        }

        // GET /api/tickets/buscar-titulo?palabra=... - Buscar tickets por título
        @GetMapping("/buscar-titulo")
        public List<TicketResponseDto> buscarPorTitulo(@RequestParam String palabra) {
                return ticketService.buscarPorTitulo(palabra);
        }
}
