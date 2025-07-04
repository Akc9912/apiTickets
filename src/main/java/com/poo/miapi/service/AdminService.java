package com.poo.miapi.service;

import com.poo.miapi.model.core.*;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Creacion de usuarios

    public Admin crearAdmin(String nombre, String apellido, String email) {
        Admin a = new Admin(nombre, apellido, email);
        return a;
    }

    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        Tecnico t = new Tecnico(nombre, apellido, email);
        return t;
    }

    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        Trabajador t = new Trabajador(nombre, apellido, email);
        return t;
    }

    // Bloqueo y desvloqueo de usuarios

    public void bloquearUsuario(int idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(true);
        usuarioRepository.save(u);
    }

    public void desbloquearUsuario(int idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(false);
        usuarioRepository.save(u);
    }

    public void desbloquearTecnico(int idTecnico) {
        Usuario tecnico = usuarioRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("Técnico no encontrado"));
        if (!(tecnico instanceof Tecnico t)) {
            throw new IllegalArgumentException("El usuario no es un técnico");
        }
        t.setBloqueado(false);
        t.reiniciarFallasYMarcas();
        usuarioRepository.save(t);
    }

    public void blanquearPassword(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    public void reabrirTicket(int idTicket, int idTecnicoSolicitante) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        Usuario tecnico = usuarioRepository.findById(idTecnicoSolicitante)
                .orElseThrow(() -> new EntityNotFoundException("Técnico no encontrado"));

        if (!(tecnico instanceof Tecnico)) {
            throw new IllegalArgumentException("El usuario no es un técnico");
        }

        ticket.setEstado(EstadoTicket.REABIERTO);
        ticketRepository.save(ticket);
    }

    public List<Ticket> filtrarTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }
}
