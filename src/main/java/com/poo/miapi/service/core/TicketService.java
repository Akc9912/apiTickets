package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;

    public TicketService(TicketRepository ticketRepository, TrabajadorRepository trabajadorRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.usuarioRepository = usuarioRepository;
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
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TicketService.class);
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
}
