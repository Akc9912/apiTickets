package com.poo.miapi.service;

import com.poo.miapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private GestorDeUsuarios gestorUsuarios;
    @Autowired
    private GestorDeTickets gestorTickets;
    @Autowired
    private NotificacionService notificacionService;

    public Trabajador crearTrabajador(String nombre) {
        Trabajador trabajador = new Trabajador(nombre);
        gestorUsuarios.agregarUsuario(trabajador);
        return trabajador;
    }

    public Tecnico crearTecnico(String nombre) {
        Tecnico tecnico = new Tecnico(nombre);
        gestorUsuarios.agregarUsuario(tecnico);
        return tecnico;
    }

    public void blanquearPassword(Usuario usuario) {
        usuario.reiniciarPassword();
    }

    public void bloquearTecnico(int idTecnico) {
        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        if (u instanceof Tecnico tecnico) {
            tecnico.setBloqueado(true);
        }
    }

    public void desbloquearTecnico(int idTecnico) {
        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        if (u instanceof Tecnico tecnico) {
            tecnico.setBloqueado(false);
            tecnico.reiniciarFallas();
        }
    }

    public void reabrirTicket(int idTicket, int idTecnico) {
        Ticket t = gestorTickets.buscarPorId(idTicket);
        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        if (t != null && u instanceof Tecnico tecnico) {
            t.reabrir(tecnico);
            notificacionService.notificarTicketReabierto(t);
        }
    }
}
