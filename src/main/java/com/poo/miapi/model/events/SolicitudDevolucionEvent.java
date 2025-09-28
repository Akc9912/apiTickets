package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando un técnico solicita la devolución de un ticket
 */
public class SolicitudDevolucionEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario tecnico;
    private final String motivoDevolucion;

    public SolicitudDevolucionEvent(Object source, Ticket ticket, Usuario tecnico, String motivoDevolucion) {
        super(source);
        this.ticket = ticket;
        this.tecnico = tecnico;
        this.motivoDevolucion = motivoDevolucion;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }
}