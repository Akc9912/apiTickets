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
import com.poo.miapi.util.PasswordHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final PasswordEncoder passwordEncoder;
    private final TecnicoService tecnicoService;
    private final TecnicoPorTicketService tecnicoPorTicketService;

    @Value("${app.default-password}")
    private String defaultPassword;

    public AdminService(
            UsuarioRepository usuarioRepository,
            TicketRepository ticketRepository,
            TecnicoRepository tecnicoRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            PasswordEncoder passwordEncoder,
            TecnicoService tecnicoService,
            TecnicoPorTicketService tecnicoPorTicketService) {
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.passwordEncoder = passwordEncoder;
        this.tecnicoService = tecnicoService;
        this.tecnicoPorTicketService = tecnicoPorTicketService;
    }

    // USUARIOS

    // Crear usuario
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto usuario) {
        validarDatosUsuario(usuario);

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        Rol rolEnum = usuario.getRol();
        if (rolEnum == null) {
            throw new IllegalArgumentException("Rol no válido: " + usuario.getRol());
        }
        Usuario nuevoUsuario = crearUsuarioPorRol(usuario);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(usuario.getApellido());
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        nuevoUsuario.setCambiarPass(true);
        nuevoUsuario.setRol(rolEnum);
        usuarioRepository.save(nuevoUsuario);
        return mapToUsuarioDto(nuevoUsuario);
    }

    // Editar usuario
    public UsuarioResponseDto editarUsuario(int id, UsuarioRequestDto usuarioDto) {
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
        // No se actualiza password aquí porque UsuarioRequestDto no tiene password
        Rol rolEnum = usuarioDto.getRol();
        if (rolEnum == null) {
            throw new IllegalArgumentException("Rol no válido: " + usuarioDto.getRol());
        }
        usuario.setRol(rolEnum);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Cambiar rol de usuario (crea nuevo usuario y da de baja lógica al anterior)
    public UsuarioResponseDto cambiarRolUsuario(int id, UsuarioRequestDto usuarioCambioRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        Rol rolEnum = usuarioCambioRol.getRol();
        if (rolEnum == null) {
            throw new IllegalArgumentException("Rol no válido: " + usuarioCambioRol.getRol());
        }
        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioCambioRol);
        nuevoUsuario.setPassword(passwordEncoder.encode(defaultPassword));
        nuevoUsuario.setCambiarPass(true);
        nuevoUsuario.setRol(rolEnum);
        usuarioRepository.save(nuevoUsuario);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(nuevoUsuario);
    }

    // Activar usuario
    public UsuarioResponseDto activarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Desactivar usuario
    public UsuarioResponseDto desactivarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Bloquear usuario
    public UsuarioResponseDto bloquearUsuario(int idUsuario) {
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
    public UsuarioResponseDto blanquearPassword(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // CONSULTAS DE USUARIOS

    public List<UsuarioResponseDto> listarTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    public UsuarioResponseDto verUsuarioPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToUsuarioDto(usuario);
    }

    public List<UsuarioResponseDto> listarUsuariosPorRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        Rol rolEnum;
        try {
            rolEnum = Rol.valueOf(rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
        return usuarioRepository.findByRol(rolEnum).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // TICKETS

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
    public TicketResponseDto reabrirTicket(int idTicket, String comentario) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + idTicket));

        if (ticket.getEstado() != EstadoTicket.FINALIZADO) {
            throw new IllegalArgumentException("El ticket no está cerrado, no se puede reabrir");
        }

        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null) {
            throw new IllegalArgumentException("No hay técnico asignado al ticket, no se puede reabrir");
        }

        TecnicoPorTicket entradaHistorial = tecnicoPorTicketRepository
                .findByTecnicoAndTicket(tecnicoActual, ticket)
                .orElseThrow(
                        () -> new IllegalArgumentException("No se encontró historial para este ticket en el técnico"));

        entradaHistorial.setEstadoFinal(EstadoTicket.REABIERTO);
        entradaHistorial.setFechaDesasignacion(LocalDateTime.now());
        entradaHistorial.setComentario(comentario);
        tecnicoPorTicketRepository.save(entradaHistorial);

        tecnicoService.marcarMarca(tecnicoActual.getId(), comentario, ticket);

        ticket.setEstado(EstadoTicket.REABIERTO);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        ticketRepository.save(ticket);

        return mapToTicketDto(ticket);
    }

    // MÉTODOS AUXILIARES PRIVADOS

    private void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (rol == Rol.SUPERADMIN) {
            throw new IllegalArgumentException("No se puede asignar rol SUPERADMIN");
        }
    }

    private Usuario crearUsuarioPorRol(UsuarioRequestDto cambiarRolDto) {
        switch (cambiarRolDto.getRol()) {
            case ADMIN:
                return crearAdmin(cambiarRolDto.getNombre(), cambiarRolDto.getApellido(), cambiarRolDto.getEmail());
            case TECNICO:
                return crearTecnico(cambiarRolDto.getNombre(), cambiarRolDto.getApellido(), cambiarRolDto.getEmail());
            case TRABAJADOR:
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
                usuario.getRol() != null ? usuario.getRol().name() : null,
                usuario.isActivo(),
                usuario.isBloqueado());
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
