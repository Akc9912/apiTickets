package com.poo.miapi.shared.events;

import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.model.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando un técnico marca un ticket como resuelto
 * y solicita evaluación del creador
 */
public class TicketEvaluacionSolicitadaEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario tecnico;
    private final String comentarioResolucion;

    public TicketEvaluacionSolicitadaEvent(Object source, Ticket ticket, Usuario tecnico, String comentarioResolucion) {
        super(source);
        this.ticket = ticket;
        this.tecnico = tecnico;
        this.comentarioResolucion = comentarioResolucion;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }
}