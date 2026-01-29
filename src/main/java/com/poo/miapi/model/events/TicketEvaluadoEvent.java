package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando el creador evalúa un ticket resuelto
 */
public class TicketEvaluadoEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario creador;
    private final Usuario tecnico;
    private final boolean aprobado;
    private final String comentarioEvaluacion;

    public TicketEvaluadoEvent(Object source, Ticket ticket, Usuario creador, Usuario tecnico,
            boolean aprobado, String comentarioEvaluacion) {
        super(source);
        this.ticket = ticket;
        this.creador = creador;
        this.tecnico = tecnico;
        this.aprobado = aprobado;
        this.comentarioEvaluacion = comentarioEvaluacion;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getCreador() {
        return creador;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public String getComentarioEvaluacion() {
        return comentarioEvaluacion;
    }
}