package com.poo.miapi.model;

import java.util.List;

public class admin extends Usuario {

    public admin(String nombre) {
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
        tecnico.setBloqueado(true);
    }

    public void desbloquearTecnico(Tecnico tecnico) {
        tecnico.setBloqueado(false);
        tecnico.reiniciarFallas();
    }

    public void blanquearPassword(Usuario usuario) {
        usuario.reiniciarPassword();
    }

    public void reabrirTicket(Ticket ticket, Tecnico tecnicoQueLoPide) {
        if (ticket != null && tecnicoQueLoPide != null) {
            ticket.reabrir(tecnicoQueLoPide);
        }
    }

    // Listar tickets por estado (filtrado externo)
    public List<Ticket> filtrarTicketsPorEstado(List<Ticket> tickets, EstadoTicket estado) {
        return tickets.stream()
                .filter(t -> t.getEstado() == estado)
                .toList();
    }
}
