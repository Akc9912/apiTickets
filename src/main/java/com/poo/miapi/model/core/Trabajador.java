package com.poo.miapi.model.core;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TRABAJADOR")
public class Trabajador extends Usuario {

    @OneToMany(mappedBy = "creador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> misTickets;

    public Trabajador() {
        super(); // Constructor vacío requerido por JPA
        this.misTickets = new ArrayList<>();
    }

    public Trabajador(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.misTickets = new ArrayList<>();
    }

    public List<Ticket> getMisTickets() {
        return misTickets;
    }

    public void agregarTicket(Ticket ticket) {
        misTickets.add(ticket);
        ticket.setCreador(this);
    }

    @Override
    public String getTipoUsuario() {
        return "TRABAJADOR";
    }
}