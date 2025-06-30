package com.poo.miapi.model;

import java.util.ArrayList;
import java.util.List;

public class Tecnico extends Usuario {

    private List<Ticket> ticketsAtendidos;
    private int fallas;
    private int marcas;
    private boolean bloqueado;

    public Tecnico(String nombre) {
        super(nombre);
        this.ticketsAtendidos = new ArrayList<>();
        this.fallas = 0;
        this.marcas = 0;
        this.bloqueado = false;
    }

    @Override
    public String getTipoUsuario() {
        return "Técnico";
    }

    public boolean estaBloqueado() {
        return bloqueado;
    }

    public int getFallas() {
        return fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public List<Ticket> getTicketsAtendidos() {
        return new ArrayList<>(ticketsAtendidos);
    }

    /**
     * Toma un ticket si no está bloqueado y no ha superado el límite de tickets
     * activos.
     */
    public void tomarTicket(Ticket ticket) {
        if (bloqueado) {
            throw new IllegalStateException("El técnico está bloqueado y no puede tomar tickets.");
        }

        if (ticketsAtendidos.size() >= 3) {
            throw new IllegalStateException("No se pueden atender más de 3 tickets simultáneamente.");
        }

        if (!ticket.puedeSerTomado()) {
            throw new IllegalStateException("El ticket no está disponible para ser tomado.");
        }

        ticket.asignarTecnico(this);
        ticketsAtendidos.add(ticket);
    }

    /**
     * Marca un ticket como resuelto.
     */
    public void resolverTicket(Ticket ticket) {
        if (!ticketsAtendidos.contains(ticket)) {
            throw new IllegalArgumentException("Este ticket no está siendo atendido por el técnico.");
        }

        ticket.marcarResuelto();
    }

    /**
     * Devuelve un ticket (renuncia a resolverlo), lo que genera una marca o falla.
     */
    public void devolverTicket(Ticket ticket) {
        if (!ticketsAtendidos.contains(ticket)) {
            throw new IllegalArgumentException("El técnico no está atendiendo este ticket.");
        }

        ticketsAtendidos.remove(ticket);
        ticket.desasignarTecnico();

        if (this.marcas > 0) {
            this.marcas--;
            this.fallas++;
        } else {
            this.marcas++;
        }

        if (this.fallas >= 3) {
            this.bloqueado = true;
        }
    }

    /**
     * Se llama cuando resuelve correctamente un ticket reabierto.
     */
    public void limpiarFalla() {
        if (this.fallas > 0) {
            this.fallas--;
        }
    }

    public void setBloqueado(boolean valor) {
        this.bloqueado = valor;
    }

    public void reiniciarFallas() {
        this.fallas = 0;
    }

    public void reiniciarMarcas() {
        this.marcas = 0;
    }
}
