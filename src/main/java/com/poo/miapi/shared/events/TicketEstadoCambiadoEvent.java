package com.poo.miapi.shared.events;

import com.poo.miapi.module.ticket.model.EstadoTicket;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.model.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando cambia el estado de un ticket
 */
public class TicketEstadoCambiadoEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final EstadoTicket estadoAnterior;
    private final EstadoTicket estadoNuevo;
    private final Usuario cambiadoPor;
    private final String observaciones;

    public TicketEstadoCambiadoEvent(Object source, Ticket ticket, EstadoTicket estadoAnterior,
            EstadoTicket estadoNuevo, Usuario cambiadoPor, String observaciones) {
        super(source);
        this.ticket = ticket;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.cambiadoPor = cambiadoPor;
        this.observaciones = observaciones;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public EstadoTicket getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoTicket getEstadoNuevo() {
        return estadoNuevo;
    }

    public Usuario getCambiadoPor() {
        return cambiadoPor;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
