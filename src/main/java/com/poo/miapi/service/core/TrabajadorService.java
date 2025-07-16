package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.trabajador.TrabajadorResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.HistorialValidacionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TecnicoService tecnicoService;
    private final HistorialValidacionRepository historialValidacionRepository;

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            TicketRepository ticketRepository,
            TecnicoRepository tecnicoRepository,
            TecnicoService tecnicoService,
            HistorialValidacionRepository historialValidacionRepository) {
        this.trabajadorRepository = trabajadorRepository;
        this.ticketRepository = ticketRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.tecnicoService = tecnicoService;
        this.historialValidacionRepository = historialValidacionRepository;
    }

    public Trabajador buscarPorId(int id) {
        return trabajadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado"));
    }

    // Crear ticket usando DTO
    public TicketResponseDto crearTicket(TicketRequestDto dto) {
        Trabajador trabajador = buscarPorId(dto.getIdTrabajador());
        Ticket ticket = new Ticket(dto.getTitulo(), dto.getDescripcion(), trabajador);
        trabajador.agregarTicket(ticket);
        Ticket saved = ticketRepository.save(ticket);
        return mapToTicketDto(saved);
    }

    // Evaluar resolución usando DTO
    public TicketResponseDto evaluarTicket(int idTicket, EvaluarTicketDto dto) {
        Trabajador trabajador = buscarPorId(dto.getIdTrabajador());
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (ticket.getTecnicoActual() == null) {
            throw new IllegalStateException("El ticket no tiene técnico asignado");
        }

        if (ticket.getCreador().getId() != trabajador.getId()) {
            throw new IllegalArgumentException("Este ticket no pertenece al trabajador");
        }

        if (!ticket.getEstado().equals(EstadoTicket.RESUELTO)) {
            throw new IllegalStateException("El ticket no está en estado RESUELTO");
        }

        if (dto.isFueResuelto()) {
            ticket.setEstado(EstadoTicket.FINALIZADO);
        } else {
            ticket.setEstado(EstadoTicket.REABIERTO);
            tecnicoService.marcarFalla(ticket.getTecnicoActual().getId(), dto.getMotivoFalla(), ticket);
        }

        ticketRepository.save(ticket);

        HistorialValidacionTrabajador validacion = new HistorialValidacionTrabajador(
                trabajador, ticket, dto.isFueResuelto(),
                dto.isFueResuelto() ? "Resuelto correctamente" : dto.getMotivoFalla());
        historialValidacionRepository.save(validacion);

        return mapToTicketDto(ticket);
    }

    // Ver tickets activos (devuelve DTOs)
    public List<TicketResponseDto> verTicketsActivos(int idTrabajador) {
        Trabajador trabajador = buscarPorId(idTrabajador);
        return trabajador.getMisTickets().stream()
                .filter(t -> !t.getEstado().equals(EstadoTicket.FINALIZADO))
                .map(this::mapToTicketDto)
                .toList();
    }

    // Ver todos mis tickets (devuelve DTOs)
    public List<TicketResponseDto> verTodosMisTickets(int idTrabajador) {
        Trabajador trabajador = buscarPorId(idTrabajador);
        return trabajador.getMisTickets().stream()
                .map(this::mapToTicketDto)
                .toList();
    }

    // Listar todos los trabajadores (devuelve DTOs)
    public List<TrabajadorResponseDto> listarTodos() {
        return trabajadorRepository.findAll().stream()
                .map(this::mapToTrabajadorDto)
                .toList();
    }

    // Métodos auxiliares para mapear entidades a DTOs
    private TicketResponseDto mapToTicketDto(Ticket ticket) {
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

    private TrabajadorResponseDto mapToTrabajadorDto(Trabajador trabajador) {
        return new TrabajadorResponseDto(
                trabajador.getId(),
                trabajador.getNombre(),
                trabajador.getApellido(),
                trabajador.getEmail(),
                trabajador.isActivo());
    }
}
