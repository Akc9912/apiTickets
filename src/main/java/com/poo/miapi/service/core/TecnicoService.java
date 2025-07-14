package com.poo.miapi.service.core;

import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.*;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TecnicoPorTicketRepository tecnicoPorTicketRepository;

    @Autowired
    private IncidenteTecnicoRepository incidenteTecnicoRepository;

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

    public void marcarFalla(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarFalla(tecnico);

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, IncidenteTecnico.TipoIncidente.FALLA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    public void marcarMarca(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarMarca(tecnico);

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, IncidenteTecnico.TipoIncidente.MARCA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    public void reiniciarFallasYMarcas(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        reiniciarFallasYMarcas(tecnico);
    }

    public TicketResponseDto tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.NO_ATENDIDO)) {
            throw new IllegalStateException("El ticket ya está siendo atendido");
        }

        ticket.setEstado(EstadoTicket.ATENDIDO);
        ticket.setTecnicoActual(tecnico);

        TecnicoPorTicket historial = new TecnicoPorTicket(ticket, tecnico, EstadoTicket.NO_ATENDIDO,
                EstadoTicket.ATENDIDO);
        tecnicoPorTicketRepository.save(historial);
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

        if (ticket.getTecnicoActual() == null || ticket.getTecnicoActual().getId() != tecnico.getId()) {
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
        return ticketRepository.findByTecnicoActual(tecnico).stream()
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
                tecnico.getRol(),
                tecnico.isCambiarPass(),
                tecnico.isActivo(),
                tecnico.isBloqueado(),
                tecnico.getFallas(),
                tecnico.getMarcas(),
                tecnico.getTicketsActuales() // Puedes mapear a DTOs si lo prefieres
        );
    }

    private IncidenteTecnicoResponseDto mapToIncidenteDto(IncidenteTecnico incidente) {
        return new IncidenteTecnicoResponseDto(
                incidente.getId(),
                incidente.getTecnico().getId(),
                incidente.getTicket().getId(),
                incidente.getTipo(),
                incidente.getMotivo(),
                incidente.getFechaRegistro());
    }
}
