package com.poo.miapi.service.estadistica;

import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.model.enums.EstadoTicket;
import org.springframework.stereotype.Service;

@Service
public class EstadisticaService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TrabajadorRepository trabajadorRepository;
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

    public int cantidadTotalTickets() {
        return (int) ticketRepository.count();
    }

    public int cantidadTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).size();
    }

    public int cantidadTotalUsuarios() {
        return (int) usuarioRepository.count();
    }

    public int cantidadTotalTecnicos() {
        return (int) tecnicoRepository.count();
    }

    public int cantidadTotalTrabajadores() {
        return (int) trabajadorRepository.count();
    }

    public int cantidadIncidentesTecnicos() {
        return (int) incidenteTecnicoRepository.count();
    }
}
