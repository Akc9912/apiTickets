package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.trabajador.TrabajadorResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.historial.HistorialValidacion;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.HistorialValidacionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoService tecnicoService;
    private final HistorialValidacionRepository historialValidacionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TrabajadorService.class);

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            TicketRepository ticketRepository,
            TecnicoService tecnicoService,
            HistorialValidacionRepository historialValidacionRepository) {
        this.trabajadorRepository = trabajadorRepository;
        this.ticketRepository = ticketRepository;
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
    Trabajador trabajador = buscarPorId(dto.getIdUsuarioValidador());
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (ticket.getUltimoTecnicoAtendio() == null) {
            throw new IllegalStateException("El ticket no tiene técnico en historial");
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
            ticket.setEstado(EstadoTicket.REABIERTO); // Primero va a REABIERTO
            tecnicoService.marcarFalla(ticket.getUltimoTecnicoAtendio().getId(), dto.getMotivoFalla(), ticket);
        }

        ticketRepository.save(ticket);

    Usuario usuarioValidador = trabajador; // El trabajador creador valida su propio ticket
    HistorialValidacion validacion = new HistorialValidacion(
        usuarioValidador,
        ticket,
        dto.isFueResuelto(),
        dto.isFueResuelto() ? "Resuelto correctamente" : dto.getMotivoFalla()
    );
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
        try {
            logger.info("[TrabajadorService] Buscando trabajador con ID: {}", idTrabajador);
            Trabajador trabajador = buscarPorId(idTrabajador);
            logger.info("[TrabajadorService] Trabajador encontrado: {}", trabajador);
            List<Ticket> tickets = trabajador.getMisTickets();
            logger.info("[TrabajadorService] Tickets encontrados: {}", tickets.size());
            List<TicketResponseDto> dtos = tickets.stream()
                    .map(ticket -> {
                        try {
                            TicketResponseDto dto = mapToTicketDto(ticket);
                            logger.info("[TrabajadorService] Ticket mapeado: {}", dto);
                            return dto;
                        } catch (Exception e) {
                            logger.error("[TrabajadorService] Error al mapear ticket: {}", ticket, e);
                            throw e;
                        }
                    })
                    .toList();
            logger.info("[TrabajadorService] Tickets mapeados: {}", dtos.size());
            return dtos;
        } catch (Exception ex) {
            logger.error("[TrabajadorService] Error al obtener tickets para trabajador {}: {}", idTrabajador, ex.getMessage(), ex);
            throw ex;
        }
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
    
