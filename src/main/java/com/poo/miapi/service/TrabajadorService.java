package com.poo.miapi.service;

import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import com.poo.miapi.repository.TrabajadorRepository;
import com.poo.miapi.repository.TecnicoRepository;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.HistorialValidacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrabajadorService {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private TecnicoService tecnicoService;

    @Autowired
    private HistorialValidacionRepository historialValidacionRepository;

    public Trabajador buscarPorId(int id) {
        return trabajadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado"));
    }

    public Ticket crearTicket(int idTrabajador, String titulo, String descripcion) {
        Trabajador trabajador = buscarPorId(idTrabajador);
        Ticket ticket = new Ticket(titulo, descripcion, trabajador);
        trabajador.agregarTicket(ticket);
        return ticketRepository.save(ticket);
    }

    public void evaluarResolucion(int idTrabajador, int idTicket, boolean fueResuelto, String motivoFalla) {
        Trabajador trabajador = buscarPorId(idTrabajador);
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

        if (fueResuelto) {
            ticket.setEstado(EstadoTicket.FINALIZADO);
        } else {
            ticket.setEstado(EstadoTicket.REABIERTO);
            tecnicoService.marcarFalla(ticket.getTecnicoActual().getId(), motivoFalla, ticket);
        }

        ticketRepository.save(ticket);

        HistorialValidacionTrabajador validacion = new HistorialValidacionTrabajador(
                trabajador, ticket, fueResuelto, fueResuelto ? "Resuelto correctamente" : motivoFalla);
        historialValidacionRepository.save(validacion);
    }

    public List<Ticket> verTicketsActivos(int idTrabajador) {
        Trabajador trabajador = buscarPorId(idTrabajador);
        return trabajador.getMisTickets().stream()
                .filter(t -> !t.getEstado().equals(EstadoTicket.FINALIZADO))
                .toList();
    }

    public List<Ticket> verTodosMisTickets(int idTrabajador) {
        Trabajador trabajador = buscarPorId(idTrabajador);
        return trabajador.getMisTickets().stream().toList();
    }

    public List<Trabajador> listarTodos() {
        return trabajadorRepository.findAll();
    }
}
