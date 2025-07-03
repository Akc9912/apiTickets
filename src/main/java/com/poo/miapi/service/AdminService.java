package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Crear trabajador
    public Trabajador crearTrabajador(String nombre) {
        Trabajador nuevo = new Trabajador(nombre);
        return usuarioRepository.save(nuevo);
    }

    // Crear técnico
    public Tecnico crearTecnico(String nombre) {
        Tecnico nuevo = new Tecnico(nombre);
        return usuarioRepository.save(nuevo);
    }

    // Bloquear técnico
    public void bloquearTecnico(int idTecnico) {
        Usuario usuario = usuarioRepository.findById(idTecnico)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
        ((Tecnico) usuario).setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    // Desbloquear técnico
    public void desbloquearTecnico(int idTecnico) {
        Usuario usuario = usuarioRepository.findById(idTecnico)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
        Tecnico tecnico = (Tecnico) usuario;
        tecnico.setBloqueado(false);
        tecnico.reiniciarFallas();
        usuarioRepository.save(tecnico);
    }

    // Blanquear password
    public void blanquearPassword(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.reiniciarPassword();
        usuarioRepository.save(usuario);
    }

    // Reabrir ticket (asignado previamente al técnico que lo pide)
    public void reabrirTicket(int idTicket, int idTecnicoQueLoPide) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));

        Usuario tecnico = usuarioRepository.findById(idTecnicoQueLoPide)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no válido"));

        ticket.reabrir((Tecnico) tecnico);
        ticketRepository.save(ticket);
    }

    // Filtrar tickets por estado
    public List<Ticket> filtrarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    // Obtener técnicos bloqueados
    public List<Tecnico> obtenerTecnicosBloqueados() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Tecnico)
                .map(u -> (Tecnico) u)
                .filter(Tecnico::estaBloqueado)
                .collect(Collectors.toList());
    }
}
