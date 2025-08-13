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

        public Object obtenerEstadisticasUsuarios() {
            // Lógica movida desde SuperAdminService
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalUsuarios", usuarioRepository.count());
            stats.put("usuariosActivos", usuarioRepository.countByActivoTrue());
            stats.put("usuariosBloqueados", usuarioRepository.countByBloqueadoTrue());
            stats.put("superAdmins", usuarioRepository.countByRol(com.poo.miapi.model.enums.Rol.SUPER_ADMIN));
            stats.put("admins", usuarioRepository.countByRol(com.poo.miapi.model.enums.Rol.ADMIN));
            stats.put("tecnicos", usuarioRepository.countByRol(com.poo.miapi.model.enums.Rol.TECNICO));
            stats.put("trabajadores", usuarioRepository.countByRol(com.poo.miapi.model.enums.Rol.TRABAJADOR));
            return stats;
        }

        public Object obtenerEstadisticasTickets() {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalTickets", ticketRepository.count());
            stats.put("ticketsNoAtendidos", ticketRepository.countByEstado(com.poo.miapi.model.enums.EstadoTicket.NO_ATENDIDO));
            stats.put("ticketsAtendidos", ticketRepository.countByEstado(com.poo.miapi.model.enums.EstadoTicket.ATENDIDO));
            stats.put("ticketsResueltos", ticketRepository.countByEstado(com.poo.miapi.model.enums.EstadoTicket.RESUELTO));
            stats.put("ticketsFinalizados", ticketRepository.countByEstado(com.poo.miapi.model.enums.EstadoTicket.FINALIZADO));
            stats.put("ticketsReabiertos", ticketRepository.countByEstado(com.poo.miapi.model.enums.EstadoTicket.REABIERTO));
            return stats;
        }

        public Object obtenerEstadisticasSistema() {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("usuarios", obtenerEstadisticasUsuarios());
            stats.put("tickets", obtenerEstadisticasTickets());
            stats.put("tecnicosBloqueados", tecnicoRepository.countByBloqueadoTrue());
            return stats;
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
