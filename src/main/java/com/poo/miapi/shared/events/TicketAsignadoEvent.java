package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando se asigna un ticket a un técnico
 */
public class TicketAsignadoEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario tecnico;
    private final Usuario asignadoPor;
    private final String motivo;

    public TicketAsignadoEvent(Object source, Ticket ticket, Usuario tecnico, Usuario asignadoPor, String motivo) {
        super(source);
        this.ticket = ticket;
        this.tecnico = tecnico;
        this.asignadoPor = asignadoPor;
        this.motivo = motivo;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public Usuario getAsignadoPor() {
        return asignadoPor;
    }

    public String getMotivo() {
        return motivo;
    }
}
