package com.poo.miapi.model.core;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@DiscriminatorValue("TRABAJADOR")
public class Trabajador extends Usuario {

    @OneToMany(mappedBy = "creador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> misTickets = new ArrayList<>();

    public Trabajador() {
        super();
        this.setRol("TRABAJADOR");
    }

    public Trabajador(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol("TRABAJADOR");
    }

    public Trabajador(String nombre, String apellido, String email, String password, boolean activo) {
        super(nombre, apellido, email, password, "TRABAJADOR", activo);
        this.setRol("TRABAJADOR");
    }

    public List<Ticket> getMisTickets() {
        return Collections.unmodifiableList(misTickets);
    }

    public void agregarTicket(Ticket ticket) {
        if (ticket != null) {
            misTickets.add(ticket);
            ticket.setCreador(this);
        }
    }

    public void eliminarTicket(Ticket ticket) {
        misTickets.remove(ticket);
        if (ticket != null) {
            ticket.setCreador(null);
        }
    }

    @Override
    public String getTipoUsuario() {
        return "TRABAJADOR";
    }
}