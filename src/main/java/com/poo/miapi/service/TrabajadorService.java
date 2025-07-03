package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrabajadorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Crear nuevo ticket
    public Ticket crearTicket(int idTrabajador, String titulo, String descripcion) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        Ticket nuevo = trabajador.crearTicket(titulo, descripcion);
        return ticketRepository.save(nuevo);
    }

    // Ver todos los tickets creados por el trabajador
    public List<Ticket> verMisTickets(int idTrabajador) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        return ticketRepository.findByCreador(trabajador);
    }

    // Ver tickets activos (no finalizados)
    public List<Ticket> verTicketsActivos(int idTrabajador) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        return ticketRepository.findByCreador(trabajador).stream()
                .filter(t -> t.getEstado() != EstadoTicket.FINALIZADO)
                .collect(Collectors.toList());
    }

    // Confirmar resolución
    public void confirmarResolucion(int idTrabajador, int idTicket, boolean fueResuelto) {
        Trabajador trabajador = getTrabajadorPorId(idTrabajador);
        Ticket ticket = getTicketPorId(idTicket);
        trabajador.confirmarResolucion(ticket, fueResuelto);
        ticketRepository.save(ticket);
    }

    // Métodos privados de acceso
    private Trabajador getTrabajadorPorId(int id) {
        return (Trabajador) usuarioRepository.findById(id)
                .filter(u -> u instanceof Trabajador)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
    }

    private Ticket getTicketPorId(int id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }
}
