package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.enums.EstadoSolicitud;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.historial.IncidenteTecnico;
import com.poo.miapi.model.historial.SolicitudDevolucion;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.repository.historial.SolicitudDevolucionRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final TecnicoRepository tecnicoRepository;
    private final IncidenteTecnicoRepository incidenteTecnicoRepository;
    private final SolicitudDevolucionRepository solicitudDevolucionRepository;

    public TicketService(
        TicketRepository ticketRepository,
        TrabajadorRepository trabajadorRepository,
        UsuarioRepository usuarioRepository,
        TecnicoPorTicketRepository tecnicoPorTicketRepository,
        TecnicoRepository tecnicoRepository,
        IncidenteTecnicoRepository incidenteTecnicoRepository,
        SolicitudDevolucionRepository solicitudDevolucionRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.incidenteTecnicoRepository = incidenteTecnicoRepository;
        this.solicitudDevolucionRepository = solicitudDevolucionRepository;
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getRol() != null && usuario.getRol().name().equals("TRABAJADOR")) {
                Optional<Trabajador> trabajadorOpt = trabajadorRepository.findByEmail(email);
                if (trabajadorOpt.isPresent()) {
                    return trabajadorOpt.get();
                }
            }
            return usuario;
        }
        return null;
    }

    public Trabajador obtenerTrabajadorPorId(int id) {
        return trabajadorRepository.findById(id).orElse(null);
    }

    public TicketResponseDto crearTicketConCreador(TicketRequestDto dto, Usuario creador) {
        Logger logger = LoggerFactory.getLogger(TicketService.class);
        logger.debug("[TicketService] Creando ticket: titulo={}, descripcion={}, creadorEmail={}, creadorRol={}", dto.getTitulo(), dto.getDescripcion(), creador != null ? creador.getEmail() : null, creador != null ? creador.getRol() : null);
        Ticket ticket = new Ticket(dto.getTitulo(), dto.getDescripcion(), creador);
        if (creador instanceof Trabajador trabajador) {
            logger.debug("[TicketService] El creador es Trabajador, agregando ticket a su lista");
            trabajador.agregarTicket(ticket);
        } else {
            logger.debug("[TicketService] El creador NO es Trabajador, no se agrega a lista de tickets de trabajador");
        }
        Ticket saved = ticketRepository.save(ticket);
        logger.info("[TicketService] Ticket guardado con id {}", saved.getId());
        return mapToDto(saved);
    }

    public TicketResponseDto buscarPorId(int id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        return mapToDto(ticket);
    }

    public List<TicketResponseDto> listarTodos() {
        return ticketRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TicketResponseDto> listarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TicketResponseDto> listarPorCreador(int idTrabajador) {
        return ticketRepository.findByCreadorId(idTrabajador).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Tickets por creador y estado (para evaluación)
    public List<TicketResponseDto> listarPorCreadorYEstado(int idTrabajador, EstadoTicket estado) {
        return ticketRepository.findByEstadoAndCreadorId(estado, idTrabajador).stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TicketResponseDto> buscarPorTitulo(String palabra) {
        return ticketRepository.findByTituloContainingIgnoreCase(palabra).stream()
                .map(this::mapToDto)
                .toList();
    }

    public TicketResponseDto actualizarEstado(int idTicket, EstadoTicket nuevoEstado) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        ticket.setEstado(nuevoEstado);
        Ticket actualizado = ticketRepository.save(ticket);
        return mapToDto(actualizado);
    }

    public TicketResponseDto guardar(Ticket ticket) {
        Ticket saved = ticketRepository.save(ticket);
        return mapToDto(saved);
    }

    // Compatibilidad: crear ticket usando idTrabajador
    public TicketResponseDto crearTicket(TicketRequestDto dto) {
        Trabajador trabajador = trabajadorRepository.findById(dto.getIdTrabajador())
            .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado"));
        return crearTicketConCreador(dto, trabajador);
    }

    private TicketResponseDto mapToDto(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getEstado(),
                ticket.getCreador() != null ? ticket.getCreador().getNombre() : null,
                ticket.getTecnicoActual() != null ? ticket.getTecnicoActual().getNombre() : null,
                ticket.getFechaCreacion(),
                ticket.getFechaUltimaActualizacion());
    }

    // Tickets no asignados y reabiertos (para técnicos)
    public List<TicketResponseDto> listarTicketsNoAsignadosYReabiertos() {
        List<Ticket> noAsignados = ticketRepository.findByEstado(EstadoTicket.NO_ATENDIDO);
        List<Ticket> reabiertos = ticketRepository.findByEstado(EstadoTicket.REABIERTO);
        return Stream.concat(noAsignados.stream(), reabiertos.stream())
                .map(this::mapToDto)
                .toList();
    }

    // Tickets asignados al técnico en estado atendido
    public List<TicketResponseDto> listarTicketsAsignadosAlTecnico(int tecnicoId) {
    Usuario usuario = usuarioRepository.findById(tecnicoId).orElse(null);
    if (!(usuario instanceof Tecnico tecnico)) return Collections.emptyList();
    List<Ticket> atendidos = ticketRepository.findByEstadoAndTecnicoActual(EstadoTicket.ATENDIDO, tecnico);
    // Filtrar tickets que NO tengan una solicitud de devolución pendiente
    return atendidos.stream()
        .filter(ticket -> {
            List<SolicitudDevolucion> solicitudes = solicitudDevolucionRepository.findByTicketId(ticket.getId());
            return solicitudes.stream().noneMatch(s -> s.getEstado() == EstadoSolicitud.PENDIENTE);
        })
        .map(this::mapToDto)
        .toList();
    }

    // Historial de todos los tickets donde participó el técnico
    public List<TicketResponseDto> listarHistorialTecnico(int tecnicoId) {
    Usuario usuario = usuarioRepository.findById(tecnicoId).orElse(null);
    if (!(usuario instanceof Tecnico)) return Collections.emptyList();
    List<TecnicoPorTicket> historial = tecnicoPorTicketRepository.findByTecnicoId(tecnicoId);
    return historial.stream()
        .map(tpt -> mapToDto(tpt.getTicket()))
        .toList();
    }

    // Lógica de negocio para determinar el creador según el rol
    public TicketResponseDto crearTicketConRol(TicketRequestDto dto, Usuario usuario) {
        Usuario creadorTicket = null;
        switch (usuario.getRol().name()) {
            case "TRABAJADOR":
                creadorTicket = usuario;
                break;
            case "ADMIN":
            case "SUPERADMIN":
                if (dto.getIdTrabajador() != 0) {
                    creadorTicket = obtenerTrabajadorPorId(dto.getIdTrabajador());
                    if (creadorTicket == null) {
                        throw new IllegalArgumentException("Trabajador no encontrado");
                    }
                } else {
                    creadorTicket = usuario;
                }
                break;
            default:
                throw new IllegalStateException("Rol no autorizado para crear tickets");
        }
        return crearTicketConCreador(dto, creadorTicket);
    }

    // Verifica si el ticket pertenece al usuario
    public boolean esTicketDeUsuario(int ticketId, int usuarioId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Ticket no encontrado"));
        return ticket.getCreador() != null && ticket.getCreador().getId() == usuarioId;
    }

    // Reabrir ticket (solo lógica, sin reglas de acceso)
    public TicketResponseDto reabrirTicket(int idTicket, String comentario, int usuarioId) {
        Ticket ticket = ticketRepository.findById(idTicket)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Ticket no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuario no encontrado"));
        if (!ticket.getEstado().equals(EstadoTicket.FINALIZADO)) {
            throw new IllegalArgumentException("El ticket no está cerrado, no se puede reabrir");
        }
        Rol rol = usuario.getRol();
        boolean esTrabajador = rol == Rol.TRABAJADOR;
        boolean esAdmin = rol == Rol.ADMIN;
        boolean esSuperAdmin = rol == Rol.SUPER_ADMIN;
        if (esTrabajador) {
            if (ticket.getCreador() == null || ticket.getCreador().getId() != usuario.getId()) {
                throw new SecurityException("No puedes reabrir tickets que no creaste");
            }
        } else if (!(esAdmin || esSuperAdmin)) {
            throw new SecurityException("No tienes permisos para reabrir tickets");
        }
        ticket.setEstado(EstadoTicket.REABIERTO);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        // Sumar falla al último técnico que atendió el ticket
        List<TecnicoPorTicket> historial = ticket.getHistorialTecnicos();
        if (!historial.isEmpty()) {
            for (int i = historial.size() - 1; i >= 0; i--) {
                TecnicoPorTicket entrada = historial.get(i);
                if (entrada.getTecnico() != null) {
                    Tecnico ultimoTecnico = entrada.getTecnico();
                    if (ultimoTecnico != null) {
                        ultimoTecnico.setFallas(ultimoTecnico.getFallas() + 1);
                        if (ultimoTecnico.getFallas() >= 3) {
                            ultimoTecnico.setBloqueado(true);
                        }
                        // Guardar el técnico actualizado
                        tecnicoRepository.save(ultimoTecnico);
                        // Registrar incidente técnico
                        IncidenteTecnico incidente = new IncidenteTecnico(ultimoTecnico, ticket, IncidenteTecnico.TipoIncidente.FALLA,
                            "Falla por ticket reabierto por trabajador");
                        incidenteTecnicoRepository.save(incidente);
                        break;
                    }
                }
            }
        }
        ticketRepository.save(ticket);
        return mapToDto(ticket);
    }
}
