package com.poo.miapi.service;

import com.poo.miapi.model.EstadoTicket;
import com.poo.miapi.model.Ticket;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestorDeTickets {
    private final List<Ticket> tickets = new ArrayList<>();

    public void registrarTicket(Ticket t) {
        tickets.add(t);
    }

    public List<Ticket> obtenerTodos() {
        return tickets;
    }

    public List<Ticket> filtrarPorEstado(EstadoTicket estado) {
        return tickets.stream()
                .filter(t -> t.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public List<Ticket> ticketsPendientes() {
        return filtrarPorEstado(EstadoTicket.NO_ATENDIDO);
    }

    public Ticket buscarPorId(int id) {
        return tickets.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
