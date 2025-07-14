package com.poo.miapi.service;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    // Buscar ticket por ID y devolver DTO
    public TicketResponseDto buscarPorId(int id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        return mapToDto(ticket);
    }

    // Listar todos los tickets como DTOs
    public List<TicketResponseDto> listarTodos() {
        return ticketRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    // Listar tickets por estado como DTOs
    public List<TicketResponseDto> listarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Listar tickets por creador como DTOs
    public List<TicketResponseDto> listarPorCreador(int idTrabajador) {
        return ticketRepository.findByCreadorId(idTrabajador).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Buscar tickets por título como DTOs
    public List<TicketResponseDto> buscarPorTitulo(String palabra) {
        return ticketRepository.findByTituloContainingIgnoreCase(palabra).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Actualizar estado y devolver DTO
    public TicketResponseDto actualizarEstado(int idTicket, EstadoTicket nuevoEstado) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        ticket.setEstado(nuevoEstado);
        Ticket actualizado = ticketRepository.save(ticket);
        return mapToDto(actualizado);
    }

    // Guardar ticket y devolver DTO
    public TicketResponseDto guardar(Ticket ticket) {
        Ticket saved = ticketRepository.save(ticket);
        return mapToDto(saved);
    }

    // Crear ticket a partir de DTO
    public TicketResponseDto crearTicket(TicketRequestDto dto) {
        // Aquí deberías buscar el trabajador y construir el Ticket
        // Ejemplo:
        // Trabajador trabajador =
        // trabajadorRepository.findById(dto.getIdTrabajador()).orElseThrow(...);
        // Ticket ticket = new Ticket(dto.getTitulo(), dto.getDescripcion(),
        // trabajador);
        // Ticket saved = ticketRepository.save(ticket);
        // return mapToDto(saved);
        // (Completa según tu lógica)
        return null; // Cambia esto por tu lógica
    }

    // Método auxiliar para mapear Ticket a TicketResponseDto
    private TicketResponseDto mapToDto(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getEstado(),
                ticket.getCreador() != null ? ticket.getCreador().getNombre() : null,
                ticket.getTecnicoActual() != null ? ticket.getTecnicoActual().getNombre() : null,
                ticket.getFechaCreacion(),
                ticket.getFechaUltimaActualizacion());
    }
}