package com.poo.miapi.service.estadistica;

import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.model.core.EstadoTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadisticaService {

    @Autowired
    private final TicketRepository ticketRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final TecnicoRepository tecnicoRepository;
    @Autowired
    private final TrabajadorRepository trabajadorRepository;
    @Autowired
    private final IncidenteTecnicoRepository incidenteTecnicoRepository;

    public EstadisticaService(
            TicketRepository ticketRepository,
            UsuarioRepository usuarioRepository,
            TecnicoRepository tecnicoRepository,
            TrabajadorRepository trabajadorRepository,
            IncidenteTecnicoRepository incidenteTecnicoRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.incidenteTecnicoRepository = incidenteTecnicoRepository;
    }

    public long cantidadTotalTickets() {
        return ticketRepository.count();
    }

    public long cantidadTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).size();
    }

    public long cantidadTotalUsuarios() {
        return usuarioRepository.count();
    }

    public long cantidadTotalTecnicos() {
        return tecnicoRepository.count();
    }

    public long cantidadTotalTrabajadores() {
        return trabajadorRepository.count();
    }

    public long cantidadIncidentesTecnicos() {
        return incidenteTecnicoRepository.count();
    }
}
