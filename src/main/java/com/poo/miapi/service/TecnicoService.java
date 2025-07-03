package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Tomar un ticket
    public void tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.tomarTicket(ticket);
        ticketRepository.save(ticket);
    }

    // Resolver un ticket
    public void resolverTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.resolverTicket(ticket);
        ticketRepository.save(ticket);
    }

    // Devolver un ticket
    public void devolverTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        Ticket ticket = getTicketPorId(idTicket);
        tecnico.devolverTicket(ticket);
        ticketRepository.save(ticket);
        usuarioRepository.save(tecnico);
    }

    // Ver tickets activos del técnico
    public List<Ticket> verTicketsAsignados(int idTecnico) {
        Tecnico tecnico = getTecnicoPorId(idTecnico);
        return ticketRepository.findByTecnicoActual(tecnico);
    }

    // Métodos internos de validación y carga
    private Tecnico getTecnicoPorId(int id) {
        return (Tecnico) usuarioRepository.findById(id)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
    }

    private Ticket getTicketPorId(int id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }
}
