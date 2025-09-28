package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando se crea un nuevo ticket
 */
public class TicketCreadoEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario creador;
    private final String observaciones;

    public TicketCreadoEvent(Object source, Ticket ticket, Usuario creador, String observaciones) {
        super(source);
        this.ticket = ticket;
        this.creador = creador;
        this.observaciones = observaciones;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getCreador() {
        return creador;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
