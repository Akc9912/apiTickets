package com.poo.miapi.model;

import java.util.ArrayList;
import java.util.List;

public class Trabajador extends Usuario {
    private List<Ticket> misTickets;

    public Trabajador(String nombre) {
        super(nombre);
        this.misTickets = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() {
        return "Trabajador";
    }

    /**
     * Crea un nuevo ticket asociado a este trabajador.
     */
    public Ticket crearTicket(String titulo, String descripcion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }

        Ticket nuevo = new Ticket(titulo, descripcion, this);
        misTickets.add(nuevo);
        return nuevo;
    }

    /**
     * Devuelve la lista de tickets del trabajador que aún no están finalizados.
     */
    public List<Ticket> verTicketsActivos() {
        List<Ticket> activos = new ArrayList<>();
        for (Ticket t : misTickets) {
            if (t.getEstado() != EstadoTicket.FINALIZADO) {
                activos.add(t);
            }
        }
        return activos;
    }

    /**
     * El trabajador confirma si el ticket fue realmente resuelto.
     */
    public void confirmarResolucion(Ticket ticket, boolean fueResuelto) {
        if (!misTickets.contains(ticket)) {
            throw new IllegalArgumentException("Este ticket no pertenece al trabajador.");
        }

        if (ticket.getEstado() != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("El ticket no está en estado 'Resuelto'.");
        }

        if (fueResuelto) {
            ticket.marcarFinalizado();
        } else {
            ticket.marcarReabierto();
        }
    }
}
