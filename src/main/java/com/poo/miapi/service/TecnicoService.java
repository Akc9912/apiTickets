package com.poo.miapi.service;

import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.*;
import com.poo.miapi.repository.TecnicoRepository;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.TecnicoPorTicketRepository;
import com.poo.miapi.repository.IncidenteTecnicoRepository;
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
        tecnico.setFallas(tecnico.getMarcas() + 1);
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
        tecnicoRepository.save(tecnico);
    }

    public void marcarMarca(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarMarca(tecnico);

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, IncidenteTecnico.TipoIncidente.MARCA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
        tecnicoRepository.save(tecnico);
    }

    public void reiniciarFallasYMarcas(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        reiniciarFallasYMarcas(tecnico);
        tecnicoRepository.save(tecnico);
    }

    public void tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.NO_ATENDIDO)) {
            throw new IllegalStateException("El ticket ya está siendo atendido");
        }

        ticket.setEstado(EstadoTicket.ATENDIDO);
        TecnicoPorTicket historial = new TecnicoPorTicket(ticket, tecnico, EstadoTicket.NO_ATENDIDO,
                EstadoTicket.ATENDIDO);
        tecnicoPorTicketRepository.save(historial);
        ticketRepository.save(ticket);
    }

    public void resolverTicket(int idTecnico, int idTicket) {
        // Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.ATENDIDO)) {
            throw new IllegalStateException("El ticket no está en estado ATENDIDO");
        }

        ticket.setEstado(EstadoTicket.RESUELTO);
        ticketRepository.save(ticket);
    }

    public void devolverTicket(int idTecnico, int idTicket, String motivo) {
        Tecnico tecnico = buscarPorId(idTecnico);
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (ticket.getTecnicoActual().getId() != tecnico.getId()) {
            throw new IllegalArgumentException("Este ticket no pertenece a este técnico");
        }

        if (ticket.getEstado() != EstadoTicket.ATENDIDO) {
            throw new IllegalStateException("Solo se pueden devolver tickets en estado ASIGNADO");
        }

        ticket.setEstado(EstadoTicket.REABIERTO);

        marcarMarca((int) tecnico.getId(), motivo, ticket);

        ticketRepository.save(ticket);
    }

    public List<IncidenteTecnico> obtenerHistorialIncidentes(int idTecnico) {
        return incidenteTecnicoRepository.findByTecnicoId(idTecnico);
    }

    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAll();
    }

    public List<Ticket> verTicketsAsignados(int idTecnico) {
        Tecnico tecnico = tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("Técnico no encontrado"));

        return ticketRepository.findByTecnicoActual(tecnico);
    }
}
