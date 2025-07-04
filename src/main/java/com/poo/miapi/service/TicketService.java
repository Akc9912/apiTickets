package com.poo.miapi.service;

import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket buscarPorId(int id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public List<Ticket> listarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    public List<Ticket> listarPorCreador(int idTrabajador) {
        return ticketRepository.findByCreadorId(idTrabajador);
    }

    public List<Ticket> buscarPorTitulo(String palabra) {
        return ticketRepository.findByTituloContainingIgnoreCase(palabra);
    }

    public Ticket actualizarEstado(int idTicket, EstadoTicket nuevoEstado) {
        Ticket ticket = buscarPorId(idTicket);
        ticket.setEstado(nuevoEstado);
        return ticketRepository.save(ticket);
    }

    public Ticket guardar(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}