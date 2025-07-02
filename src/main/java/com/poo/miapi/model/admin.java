package com.poo.miapi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario {

    public Admin() {
        super(); // Constructor requerido por JPA
    }

    public Admin(String nombre) {
        super(nombre);
    }

    @Override
    public String getTipoUsuario() {
        return "Admin";
    }

    // Métodos funcionales

    public Trabajador crearTrabajador(String nombre) {
        return new Trabajador(nombre);
    }

    public Tecnico crearTecnico(String nombre) {
        return new Tecnico(nombre);
    }

    public void bloquearTecnico(Tecnico tecnico) {
        if (tecnico != null) {
            tecnico.setBloqueado(true);
        }
    }

    public void desbloquearTecnico(Tecnico tecnico) {
        if (tecnico != null) {
            tecnico.setBloqueado(false);
            tecnico.reiniciarFallas();
        }
    }

    public void blanquearPassword(Usuario usuario) {
        if (usuario != null) {
            usuario.reiniciarPassword();
        }
    }

    public void reabrirTicket(Ticket ticket, Tecnico tecnicoQueLoPide) {
        if (ticket != null && tecnicoQueLoPide != null) {
            ticket.reabrir(tecnicoQueLoPide);
        }
    }

    public List<Ticket> filtrarTicketsPorEstado(List<Ticket> tickets, EstadoTicket estado) {
        return tickets.stream()
                .filter(t -> t.getEstado() == estado)
                .toList();
    }
}
