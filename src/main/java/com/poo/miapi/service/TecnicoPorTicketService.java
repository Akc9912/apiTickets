package com.poo.miapi.service;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.TecnicoPorTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TecnicoPorTicketService {

    @Autowired
    private TecnicoPorTicketRepository tecnicoPorTicketRepository;

    public TecnicoPorTicket buscarEntradaHistorialPorTicket(Tecnico tecnico, Ticket ticket) {
        return tecnicoPorTicketRepository.findByTecnicoAndTicket(tecnico, ticket)
                .orElse(null);
    }

    public TecnicoPorTicket guardar(TecnicoPorTicket historial) {
        return tecnicoPorTicketRepository.save(historial);
    }
}