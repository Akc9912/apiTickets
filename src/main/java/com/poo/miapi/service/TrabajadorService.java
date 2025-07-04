package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrabajadorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Crear nuevo ticket
    public Ticket crearTicket(int idTrabajador, String titulo, String descripcion) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        Ticket nuevo = trabajador.crearTicket(titulo, descripcion);
        return ticketRepository.save(nuevo);
    }

    // Ver todos los tickets creados por el trabajador
    public List<Ticket> verMisTickets(int idTrabajador) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        return ticketRepository.findByCreador(trabajador);
    }

    // Ver tickets activos (no finalizados)
    public List<Ticket> verTicketsActivos(int idTrabajador) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        return ticketRepository.findByCreador(trabajador).stream()
                .filter(t -> t.getEstado() != EstadoTicket.FINALIZADO)
                .collect(Collectors.toList());
    }

    // Confirmar resolución
    public void confirmarResolucion(int idTrabajador, int idTicket, boolean fueResuelto) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        Ticket ticket = getTicketPorId(idTicket);
        trabajador.confirmarResolucion(ticket, fueResuelto);
        ticketRepository.save(ticket);
    }

    // Métodos privados de acceso
    private Trabajador getTrabajadorPorId(int id) {
        return (Trabajador) usuarioRepository.findById(id)
                .filter(u -> u instanceof Trabajador)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
    }

    private Ticket getTicketPorId(int id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }

    public void agregarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo.");
        }
        misTickets.add(ticket);
        ticket.setCreador(this); // servicio
    }

    // Crea un nuevo ticket asociado a este trabajador
    public Ticket crearTicket(String titulo, String descripcion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }

        Ticket nuevo = new Ticket(titulo, descripcion, this);
        misTickets.add(nuevo);
        return nuevo;
    }

    // Devuelve la lista de tickets del trabajador que aún no están finalizados
    public List<Ticket> verTicketsActivos() {
        List<Ticket> activos = new ArrayList<>();
        for (Ticket t : misTickets) {
            if (t.getEstado() != EstadoTicket.FINALIZADO) {
                activos.add(t);
            }
        }
        return activos;
    }

    // El trabajador confirma si el ticket fue realmente resuelto
    public void confirmarResolucion(Ticket ticket, boolean fueResuelto) {
        if (!misTickets.contains(ticket)) {
            throw new IllegalArgumentException("Este ticket no pertenece al trabajador.");
        }

        if (ticket.getEstado() != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("El ticket no está en estado 'Resuelto'.");
        }

        if (fueResuelto) {
            ticket.marcarFinalizado();
        } else {
            ticket.marcarReabierto();
        }
    }
}
