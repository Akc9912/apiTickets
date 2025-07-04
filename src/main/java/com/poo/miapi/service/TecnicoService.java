package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Tomar un ticket
    public void tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.tomarTicket(ticket);
        ticketRepository.save(ticket);
    }

    // Resolver un ticket
    public void resolverTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.resolverTicket(ticket);
        ticketRepository.save(ticket);
    }

    // Devolver un ticket
    public void devolverTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.devolverTicket(ticket);
        ticketRepository.save(ticket);
        usuarioRepository.save(tecnico);
    }

    // Ver tickets activos del técnico
    public List<Ticket> verTicketsAsignados(int idTecnico) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        return ticketRepository.findByTecnicoActual(tecnico);
    }

    // Métodos internos de validación y carga
    private Tecnico getTecnicoPorId(int id) {
        return (Tecnico) usuarioRepository.findById(id)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
    }

    private Ticket getTicketPorId(int id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }

    public void reiniciarFallas() {
        this.fallas = 0;
    }

    public void reiniciarMarcas() {
        this.marcas = 0;
    }

    public void tomarTicket(Ticket ticket) {
        if (bloqueado) {
            throw new IllegalStateException("El técnico está bloqueado y no puede tomar tickets.");
        }

        if (ticketsAtendidos.size() >= 3) {
            throw new IllegalStateException("No se pueden atender más de 3 tickets simultáneamente.");
        }

        if (!ticket.puedeSerTomado()) {
            throw new IllegalStateException("El ticket no está disponible para ser tomado.");
        }

        ticket.asignarTecnico(this);
        ticketsAtendidos.add(ticket);
    }

    public void resolverTicket(Ticket ticket) {
        if (!ticketsAtendidos.contains(ticket)) {
            throw new IllegalArgumentException("Este ticket no está siendo atendido por el técnico.");
        }

        ticket.marcarResuelto();
    }

    public void devolverTicket(Ticket ticket) {
        if (!ticketsAtendidos.contains(ticket)) {
            throw new IllegalArgumentException("El técnico no está atendiendo este ticket.");
        }

        ticketsAtendidos.remove(ticket);
        ticket.desasignarTecnico();

        if (this.marcas > 0) {
            this.marcas--;
            this.fallas++;
        } else {
            this.marcas++;
        }

        if (this.fallas >= 3) {
            this.bloqueado = true;
        }
    }

    public void limpiarFalla() {
        if (this.fallas > 0) {
            this.fallas--;
        }
    }
}
