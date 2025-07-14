package com.poo.miapi.service.core;

import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;
import com.poo.miapi.service.historial.TecnicoPorTicketService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TecnicoRepository tecnicoRepository;
    @Autowired
    private TecnicoPorTicketRepository tecnicoPorTicketRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TecnicoService tecnicoService;
    @Autowired
    private TecnicoPorTicketService tecnicoPorTicketService;

    // =========================
    // USUARIOS
    // =========================

    // Crear usuario
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto usuario) {
        validarDatosUsuario(usuario);

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuario);
        nuevoUsuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        nuevoUsuario.setRol(usuario.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        return mapToUsuarioDto(nuevoUsuario);
    }

    // Editar usuario
    public UsuarioResponseDto editarUsuario(Long id, UsuarioRequestDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        validarDatosUsuario(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail()) &&
                !usuario.getEmail().equals(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        usuario.setRol(usuarioDto.getRol().toUpperCase());
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(usuario);
    }

    // Cambiar rol de usuario (crea nuevo usuario y da de baja lógica al anterior)
    public UsuarioResponseDto cambiarRolUsuario(int id, UsuarioRequestDto usuarioCambioRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        validarRol(usuarioCambioRol.getRol());

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioCambioRol);
        nuevoUsuario.setPassword(passwordEncoder.encode(usuarioCambioRol.getPassword()));
        nuevoUsuario.setRol(usuarioCambioRol.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(nuevoUsuario);
    }

    // Activar usuario
    public UsuarioResponseDto activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Desactivar usuario
    public UsuarioResponseDto desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Bloquear usuario
    public UsuarioResponseDto bloquearUsuario(long idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(true);
        usuarioRepository.save(u);
        return mapToUsuarioDto(u);
    }

    // Desbloquear usuario
    public UsuarioResponseDto desbloquearUsuario(int idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(false);
        usuarioRepository.save(u);
        if (u instanceof Tecnico t) {
            tecnicoService.reiniciarFallasYMarcas(t);
        }
        return mapToUsuarioDto(u);
    }

    // Blanquear password
    public UsuarioResponseDto blanquearPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // =========================
    // CONSULTAS DE USUARIOS
    // =========================

    public List<UsuarioResponseDto> listarTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    public UsuarioResponseDto verUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToUsuarioDto(usuario);
    }

    public List<UsuarioResponseDto> listarUsuariosPorRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        return usuarioRepository.findByRol(rol.toUpperCase()).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // =========================
    // TICKETS
    // =========================

    public List<TicketResponseDto> listarTodosLosTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToTicketDto)
                .toList();
    }

    public List<TicketResponseDto> filtrarTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).stream()
                .map(this::mapToTicketDto)
                .toList();
    }

    // Reabrir ticket
    public TicketResponseDto reabrirTicket(Integer idTicket, String comentario) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + idTicket));

        if (ticket.getEstado() != EstadoTicket.FINALIZADO) {
            throw new IllegalArgumentException("El ticket no está cerrado, no se puede reabrir");
        }

        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null) {
            throw new IllegalArgumentException("No hay técnico asignado al ticket, no se puede reabrir");
        }

        TecnicoPorTicket entradaHistorial = tecnicoPorTicketService
                .buscarEntradaHistorialPorTicket(tecnicoActual, ticket)
                .orElseThrow(
                        () -> new IllegalArgumentException("No se encontró historial para este ticket en el técnico"));

        entradaHistorial.setEstadoFinal(EstadoTicket.REABIERTO);
        entradaHistorial.setFechaDesasignacion(LocalDateTime.now());
        entradaHistorial.setComentario(comentario);
        tecnicoPorTicketRepository.save(entradaHistorial);

        tecnicoActual.setFallas(tecnicoActual.getFallas() + 1);
        tecnicoRepository.save(tecnicoActual);

        ticket.setEstado(EstadoTicket.REABIERTO);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        ticketRepository.save(ticket);

        return mapToTicketDto(ticket);
    }

    // =========================
    // MÉTODOS AUXILIARES PRIVADOS
    // =========================

    private void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getPassword() == null ||
                usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    private void validarRol(String rol) {
        if (!rol.equalsIgnoreCase("ADMIN") &&
                !rol.equalsIgnoreCase("TECNICO") &&
                !rol.equalsIgnoreCase("TRABAJADOR")) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
    }

    private Usuario crearUsuarioPorRol(UsuarioRequestDto cambiarRolDto) {
        switch (cambiarRolDto.getRol().toUpperCase()) {
            case "ADMIN":
                return crearAdmin(cambiarRolDto.getNombre(), cambiarRolDto.getApellido(), cambiarRolDto.getEmail());
            case "TECNICO":
                return crearTecnico(cambiarRolDto.getNombre(), cambiarRolDto.getApellido(), cambiarRolDto.getEmail());
            case "TRABAJADOR":
                return crearTrabajador(cambiarRolDto.getNombre(), cambiarRolDto.getApellido(),
                        cambiarRolDto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + cambiarRolDto.getRol());
        }
    }

    public Admin crearAdmin(String nombre, String apellido, String email) {
        return new Admin(nombre, apellido, email);
    }

    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        return new Tecnico(nombre, apellido, email);
    }

    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        return new Trabajador(nombre, apellido, email);
    }

    // Mapeo de entidad Usuario a DTO
    private UsuarioResponseDto mapToUsuarioDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo());
    }

    // Mapeo de entidad Ticket a DTO
    private TicketResponseDto mapToTicketDto(Ticket ticket) {
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
