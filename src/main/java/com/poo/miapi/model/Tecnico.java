package com.poo.miapi.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TECNICO")
public class Tecnico extends Usuario {

    @OneToMany(mappedBy = "tecnicoActual", cascade = CascadeType.ALL)
    private List<Ticket> ticketsAtendidos = new ArrayList<>();

    private int fallas = 0;
    private int marcas = 0;
    private boolean bloqueado = false;

    public Tecnico() {
        super(); // Constructor requerido por JPA
    }

    public Tecnico(String nombre) {
        super(nombre);
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

    public void setBloqueado(boolean valor) {
        this.bloqueado = valor;
    }

    public void reiniciarFallas() {
        this.fallas = 0;
    }

    public void reiniciarMarcas() {
        this.marcas = 0;
    }

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

    public void resolverTicket(Ticket ticket) {
        if (!ticketsAtendidos.contains(ticket)) {
            throw new IllegalArgumentException("Este ticket no está siendo atendido por el técnico.");
        }

        ticket.marcarResuelto();
    }

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

    public void limpiarFalla() {
        if (this.fallas > 0) {
            this.fallas--;
        }
    }
}
