package com.poo.miapi.service.core;

import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.historial.*;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final IncidenteTecnicoRepository incidenteTecnicoRepository;

    public TecnicoService(
            TecnicoRepository tecnicoRepository,
            TicketRepository ticketRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            IncidenteTecnicoRepository incidenteTecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.ticketRepository = ticketRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.incidenteTecnicoRepository = incidenteTecnicoRepository;
    }

    public Tecnico buscarPorId(int idTecnico) {
        return tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("Técnico no encontrado"));
    }

    public void sumarFalla(Tecnico tecnico) {
        tecnico.setFallas(tecnico.getFallas() + 1);
        if (tecnico.getFallas() >= 3) {
            tecnico.setBloqueado(true);
        }
        tecnicoRepository.save(tecnico);
    }

    public void sumarMarca(Tecnico tecnico) {
        tecnico.setMarcas(tecnico.getMarcas() + 1);
        if (tecnico.getMarcas() >= 3) {
            sumarFalla(tecnico);
        }
        tecnicoRepository.save(tecnico);
    }

    public void reiniciarFallasYMarcas(Tecnico tecnico) {
        tecnico.setFallas(0);
        tecnico.setMarcas(0);
        tecnico.setBloqueado(false);
        tecnicoRepository.save(tecnico);
    }

    public void reiniciarFallasYMarcas(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        reiniciarFallasYMarcas(tecnico);
    }

    public void marcarFalla(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarFalla(tecnico);

        if (tecnico.getFallas() >= 3) {
            tecnico.setBloqueado(true);
        }

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, IncidenteTecnico.TipoIncidente.FALLA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    public void marcarMarca(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarMarca(tecnico);

        if (tecnico.getMarcas() >= 3) {
            tecnico.setMarcas(0);
            motivo = motivo + " - Marcas acumuladas";
            marcarFalla(idTecnico, motivo, ticket);
        }

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, IncidenteTecnico.TipoIncidente.MARCA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    public TicketResponseDto tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.NO_ATENDIDO) && 
            !ticket.getEstado().equals(EstadoTicket.REABIERTO)) {
            throw new IllegalStateException("El ticket ya está siendo atendido o no está disponible");
        }

        EstadoTicket estadoAnterior = ticket.getEstado();
        ticket.setEstado(EstadoTicket.ATENDIDO);

        TecnicoPorTicket historial = new TecnicoPorTicket(ticket, tecnico, estadoAnterior,
                EstadoTicket.ATENDIDO, null);
        tecnicoPorTicketRepository.save(historial);

        ticket.agregarEntradaHistorial(historial);
        ticketRepository.save(ticket);

        return mapToTicketDto(ticket);
    }

    public TicketResponseDto finalizarTicket(int idTecnico, int idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.ATENDIDO)) {
            throw new IllegalStateException("El ticket no está en estado ATENDIDO");
        }

        ticket.setEstado(EstadoTicket.RESUELTO);
        ticketRepository.save(ticket);

        return mapToTicketDto(ticket);
    }

    public TicketResponseDto devolverTicket(int idTecnico, int idTicket, String motivo) {
        Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        // Usa el método utilitario del modelo para obtener el técnico actual
        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null || tecnicoActual.getId() != tecnico.getId()) {
            throw new IllegalArgumentException("Este ticket no pertenece a este técnico");
        }

        if (!ticket.getEstado().equals(EstadoTicket.ATENDIDO)) {
            throw new IllegalStateException("Solo se pueden devolver tickets en estado ATENDIDO");
        }

        ticket.setEstado(EstadoTicket.REABIERTO);
        marcarMarca(tecnico.getId(), motivo, ticket);
        ticketRepository.save(ticket);

        return mapToTicketDto(ticket);
    }

    // Devuelve historial de incidentes como DTOs
    public List<IncidenteTecnicoResponseDto> obtenerHistorialIncidentes(int idTecnico) {
        return incidenteTecnicoRepository.findByTecnicoId(idTecnico).stream()
                .map(this::mapToIncidenteDto)
                .toList();
    }

    // Devuelve todos los técnicos como DTOs
    public List<TecnicoResponseDto> listarTodos() {
        return tecnicoRepository.findAll().stream()
                .map(this::mapToTecnicoDto)
                .toList();
    }

    // Devuelve tickets asignados como DTOs
    public List<TicketResponseDto> verTicketsAsignados(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        return tecnico.getTicketsActuales().stream()
                .map(this::mapToTicketDto)
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

    private TecnicoResponseDto mapToTecnicoDto(Tecnico tecnico) {
        return new TecnicoResponseDto(
                tecnico.getId(),
                tecnico.getNombre(),
                tecnico.getApellido(),
                tecnico.getEmail(),
                tecnico.getRol() != null ? tecnico.getRol().name() : null,
                tecnico.isCambiarPass(),
                tecnico.isActivo(),
                tecnico.isBloqueado(),
                tecnico.getFallas(),
                tecnico.getMarcas(),
                tecnico.getIncidentes().stream()
                        .map(this::mapToIncidenteDto)
                        .toList());
    }

    private IncidenteTecnicoResponseDto mapToIncidenteDto(IncidenteTecnico incidente) {
        return new IncidenteTecnicoResponseDto(
                incidente.getId(),
                incidente.getTecnico().getId(),
                incidente.getTicket() != null ? incidente.getTicket().getId() : null,
                incidente.getTipo(),
                incidente.getMotivo(),
                incidente.getFechaRegistro());
    }
}
