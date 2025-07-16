package com.poo.miapi.service.core;

import com.poo.miapi.constants.UserRole;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SuperAdminService {

    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final PasswordEncoder passwordEncoder;
    private final TecnicoService tecnicoService;
    private final TecnicoPorTicketService tecnicoPorTicketService;

    @Value("${APP_DEFAULT_PASSWORD}")
    private String defaultPassword;

    public SuperAdminService(
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

    // === GESTIÓN DE USUARIOS ===

    public UsuarioResponseDto crearUsuario(UsuarioRequestDto usuarioDto) {
        validarDatosUsuario(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioDto);
        nuevoUsuario.setPassword(passwordEncoder.encode(defaultPassword));
        nuevoUsuario.setCambiarPass(true);
        nuevoUsuario.setRol(usuarioDto.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        return mapToUsuarioDto(nuevoUsuario);
    }

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

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar que no sea el último SuperAdmin
        if (UserRole.isSuperAdmin(usuario.getRol())) {
            long totalSuperAdmins = usuarioRepository.countByRol(UserRole.SUPER_ADMIN);
            if (totalSuperAdmins <= 1) {
                throw new IllegalStateException("No se puede eliminar el último SuperAdmin del sistema");
            }
        }

        usuarioRepository.delete(usuario);
    }

    public UsuarioResponseDto activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar que no sea el último SuperAdmin activo
        if (UserRole.isSuperAdmin(usuario.getRol()) && usuario.isActivo()) {
            long superAdminsActivos = usuarioRepository.countByRolAndActivoTrue(UserRole.SUPER_ADMIN);
            if (superAdminsActivos <= 1) {
                throw new IllegalStateException("No se puede desactivar el último SuperAdmin activo");
            }
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto bloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Los SuperAdmins no pueden ser bloqueados
        if (UserRole.isSuperAdmin(usuario.getRol())) {
            throw new IllegalStateException("No se puede bloquear a un SuperAdmin");
        }

        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);
        if (usuario instanceof Tecnico t) {
            tecnicoService.reiniciarFallasYMarcas(t);
        }
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto resetearPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto cambiarRolUsuario(Long id, UsuarioRequestDto usuarioCambioRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        validarRol(usuarioCambioRol.getRol());

        // Verificar restricciones de SuperAdmin
        if (UserRole.isSuperAdmin(usuario.getRol()) && !UserRole.isSuperAdmin(usuarioCambioRol.getRol())) {
            long totalSuperAdmins = usuarioRepository.countByRol(UserRole.SUPER_ADMIN);
            if (totalSuperAdmins <= 1) {
                throw new IllegalStateException("No se puede cambiar el rol del último SuperAdmin");
            }
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioCambioRol);
        nuevoUsuario.setPassword(passwordEncoder.encode(defaultPassword));
        nuevoUsuario.setCambiarPass(true);
        nuevoUsuario.setRol(usuarioCambioRol.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(nuevoUsuario);
    }

    public List<UsuarioResponseDto> listarUsuariosPorRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        return usuarioRepository.findByRol(rol.toUpperCase()).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // === GESTIÓN DE ADMINISTRADORES ===

    public List<UsuarioResponseDto> listarAdministradores() {
        return usuarioRepository.findByRolIn(List.of(UserRole.SUPER_ADMIN, UserRole.ADMIN)).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    public UsuarioResponseDto promoverAAdmin(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (UserRole.isAdminRole(usuario.getRol())) {
            throw new IllegalStateException("El usuario ya tiene permisos de administración");
        }

        UsuarioRequestDto adminDto = new UsuarioRequestDto();
        adminDto.setNombre(usuario.getNombre());
        adminDto.setApellido(usuario.getApellido());
        adminDto.setEmail(usuario.getEmail());
        adminDto.setPassword(defaultPassword);
        adminDto.setRol(UserRole.ADMIN);

        return cambiarRolUsuario(id, adminDto);
    }

    public UsuarioResponseDto degradarAdmin(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!UserRole.ADMIN.equals(usuario.getRol())) {
            throw new IllegalStateException("Solo se pueden degradar usuarios con rol ADMIN");
        }

        UsuarioRequestDto trabajadorDto = new UsuarioRequestDto();
        trabajadorDto.setNombre(usuario.getNombre());
        trabajadorDto.setApellido(usuario.getApellido());
        trabajadorDto.setEmail(usuario.getEmail());
        trabajadorDto.setPassword(defaultPassword);
        trabajadorDto.setRol(UserRole.TRABAJADOR);

        return cambiarRolUsuario(id, trabajadorDto);
    }

    // === GESTIÓN DEL SISTEMA ===

    public List<TicketResponseDto> listarTodosLosTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToTicketDto)
                .toList();
    }

    public TicketResponseDto reabrirTicket(Long idTicket, String comentario) {
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

    public void eliminarTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        ticketRepository.delete(ticket);
    }

    // === ESTADÍSTICAS ===

    public Map<String, Object> obtenerEstadisticasUsuarios() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("usuariosActivos", usuarioRepository.countByActivoTrue());
        stats.put("usuariosBloqueados", usuarioRepository.countByBloqueadoTrue());
        stats.put("superAdmins", usuarioRepository.countByRol(UserRole.SUPER_ADMIN));
        stats.put("admins", usuarioRepository.countByRol(UserRole.ADMIN));
        stats.put("tecnicos", usuarioRepository.countByRol(UserRole.TECNICO));
        stats.put("trabajadores", usuarioRepository.countByRol(UserRole.TRABAJADOR));
        return stats;
    }

    public Map<String, Object> obtenerEstadisticasTickets() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTickets", ticketRepository.count());
        stats.put("ticketsNoAtendidos", ticketRepository.countByEstado(EstadoTicket.NO_ATENDIDO));
        stats.put("ticketsAtendidos", ticketRepository.countByEstado(EstadoTicket.ATENDIDO));
        stats.put("ticketsResueltos", ticketRepository.countByEstado(EstadoTicket.RESUELTO));
        stats.put("ticketsFinalizados", ticketRepository.countByEstado(EstadoTicket.FINALIZADO));
        stats.put("ticketsReabiertos", ticketRepository.countByEstado(EstadoTicket.REABIERTO));
        return stats;
    }

    public Map<String, Object> obtenerEstadisticasSistema() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usuarios", obtenerEstadisticasUsuarios());
        stats.put("tickets", obtenerEstadisticasTickets());
        stats.put("tecnicosBloqueados", tecnicoRepository.countByBloqueadoTrue());
        return stats;
    }

    // === MÉTODOS AUXILIARES ===

    private void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getPassword() == null ||
                usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    private void validarRol(String rol) {
        if (!UserRole.isValidRole(rol)) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
    }

    private Usuario crearUsuarioPorRol(UsuarioRequestDto usuarioDto) {
        switch (usuarioDto.getRol().toUpperCase()) {
            case "SUPER_ADMIN":
                return crearSuperAdmin(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case "ADMIN":
                return crearAdmin(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case "TECNICO":
                return crearTecnico(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case "TRABAJADOR":
                return crearTrabajador(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + usuarioDto.getRol());
        }
    }

    public SuperAdmin crearSuperAdmin(String nombre, String apellido, String email) {
        return new SuperAdmin(nombre, apellido, email);
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
