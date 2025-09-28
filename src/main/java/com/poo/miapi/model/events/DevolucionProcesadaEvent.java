package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando un administrador procesa una solicitud de
 * devolución de ticket
 */
public class DevolucionProcesadaEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario admin;
    private final Usuario tecnico;
    private final Usuario trabajador;
    private final boolean aprobada;
    private final String comentarioAdmin;

    public DevolucionProcesadaEvent(Object source, Ticket ticket, Usuario admin, Usuario tecnico,
            Usuario trabajador, boolean aprobada, String comentarioAdmin) {
        super(source);
        this.ticket = ticket;
        this.admin = admin;
        this.tecnico = tecnico;
        this.trabajador = trabajador;
        this.aprobada = aprobada;
        this.comentarioAdmin = comentarioAdmin;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public Usuario getTrabajador() {
        return trabajador;
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }
}