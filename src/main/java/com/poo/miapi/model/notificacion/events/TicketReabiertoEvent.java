package com.poo.miapi.model.notificacion.events;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando un administrador reabre un ticket finalizado
 */
public class TicketReabiertoEvent extends ApplicationEvent {

    private final Ticket ticket;
    private final Usuario admin;
    private final Usuario trabajadorOriginal;
    private final Usuario ultimoTecnico;
    private final String motivoReapertura;

    public TicketReabiertoEvent(Object source, Ticket ticket, Usuario admin, Usuario trabajadorOriginal,
            Usuario ultimoTecnico, String motivoReapertura) {
        super(source);
        this.ticket = ticket;
        this.admin = admin;
        this.trabajadorOriginal = trabajadorOriginal;
        this.ultimoTecnico = ultimoTecnico;
        this.motivoReapertura = motivoReapertura;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public Usuario getTrabajadorOriginal() {
        return trabajadorOriginal;
    }

    public Usuario getUltimoTecnico() {
        return ultimoTecnico;
    }

    public String getMotivoReapertura() {
        return motivoReapertura;
    }
}
