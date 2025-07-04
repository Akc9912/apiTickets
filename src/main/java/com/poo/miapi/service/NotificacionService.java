package com.poo.miapi.service;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;

import org.springframework.stereotype.Service;

@Service
public class NotificacionService {
    public void notificarTicketTomado(Tecnico tecnico, Ticket ticket) {
        System.out.println("🔔 [NOTIFICACIÓN] Técnico " + tecnico.getNombre() +
                " tomó el ticket #" + ticket.getId() + ": " + ticket.getTitulo());
    }

    public void notificarTicketResuelto(Tecnico tecnico, Ticket ticket) {
        System.out.println("✅ [NOTIFICACIÓN] Técnico " + tecnico.getNombre() +
                " resolvió el ticket #" + ticket.getId());
    }

    public void notificarTicketDevuelto(Tecnico tecnico, Ticket ticket) {
        System.out.println("↩️ [NOTIFICACIÓN] Técnico " + tecnico.getNombre() +
                " devolvió el ticket #" + ticket.getId());
    }

    public void notificarTicketReabierto(Ticket ticket) {
        System.out.println("🚨 [NOTIFICACIÓN] El ticket #" + ticket.getId() +
                " fue reabierto por el administrador.");
    }
}
