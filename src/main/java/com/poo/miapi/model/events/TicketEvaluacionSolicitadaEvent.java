package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
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