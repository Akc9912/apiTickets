package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;

import jakarta.persistence.EntityNotFoundException;
import com.poo.miapi.util.PasswordHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
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
    private final AuditoriaService auditoriaService;

    @Value("${app.default-password}")
    private String defaultPassword;

    public SuperAdminService(
            UsuarioRepository usuarioRepository,
            TicketRepository ticketRepository,
            TecnicoRepository tecnicoRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            PasswordEncoder passwordEncoder,
            TecnicoService tecnicoService,
            AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.passwordEncoder = passwordEncoder;
        this.tecnicoService = tecnicoService;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Crear nuevo usuario
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto usuarioDto) {
        validarDatosUsuario(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // Si el rol es SUPER_ADMIN, asegurarse de que no exista otro
        if (usuarioDto.getRol() != null && usuarioDto.getRol() == Rol.SUPER_ADMIN) {
            long superAdmins = usuarioRepository.countByRol(Rol.SUPER_ADMIN);
            if (superAdmins > 0) {
                throw new IllegalStateException("Ya existe un SuperAdmin en el sistema. No se puede crear otro.");
            }
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioDto);
        // Usar PasswordHelper para generar la contraseña por defecto
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(usuarioDto.getApellido());
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        nuevoUsuario.setCambiarPass(true);
        nuevoUsuario.setRol(usuarioDto.getRol());
        usuarioRepository.save(nuevoUsuario);
        return mapToUsuarioDto(nuevoUsuario);
    }

    // Listar todos los usuarios
    public List<UsuarioResponseDto> listarTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // Ver usuario por ID
    public UsuarioResponseDto verUsuarioPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToUsuarioDto(usuario);
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
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(usuarioDto.getApellido());
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setRol(usuarioDto.getRol());
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(usuario);
    }

    // Eliminar usuario
    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar que no sea el último SuperAdmin
        if (usuario.getRol() == Rol.SUPER_ADMIN) {
            long totalSuperAdmins = usuarioRepository.countByRol(Rol.SUPER_ADMIN);
            if (totalSuperAdmins <= 1) {
                throw new IllegalStateException("No se puede eliminar el último SuperAdmin del sistema");
            }
        }

        usuarioRepository.delete(usuario);

        // Auditar eliminación de usuario
        auditoriaService.registrarAccion(
                null, // No tenemos usuario superadmin aquí
                AccionAuditoria.DELETE,
                "USUARIO",
                id,
                "Usuario eliminado: " + usuario.getEmail() + " (" + usuario.getRol() + ")",
                usuario,
                null,
                CategoriaAuditoria.SECURITY,
                SeveridadAuditoria.CRITICAL);
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

        // Verificar que no sea el último SuperAdmin activo
        if (usuario.getRol() == Rol.SUPER_ADMIN && usuario.isActivo()) {
            long superAdminsActivos = usuarioRepository
                    .countByRolAndActivoTrue(Rol.SUPER_ADMIN);
            if (superAdminsActivos <= 1) {
                throw new IllegalStateException("No se puede desactivar el último SuperAdmin activo");
            }
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Bloquear usuario
    public UsuarioResponseDto bloquearUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Los SuperAdmins no pueden ser bloqueados
        if (usuario.getRol() == Rol.SUPER_ADMIN) {
            throw new IllegalStateException("No se puede bloquear a un SuperAdmin");
        }

        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Desbloquear usuario
    public UsuarioResponseDto desbloquearUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);
        if (usuario instanceof Tecnico t) {
            tecnicoService.reiniciarFallasYMarcas(t);
        }
        return mapToUsuarioDto(usuario);
    }

    // Resetear contraseña
    public UsuarioResponseDto resetearPassword(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Cambiar rol de usuario
    public UsuarioResponseDto cambiarRolUsuario(int id, UsuarioRequestDto usuarioCambioRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        validarRol(usuarioCambioRol.getRol());

        // Verificar restricciones de SuperAdmin
        if (usuario.getRol() == Rol.SUPER_ADMIN
                && usuarioCambioRol.getRol() != Rol.SUPER_ADMIN) {
            long totalSuperAdmins = usuarioRepository.countByRol(Rol.SUPER_ADMIN);
            if (totalSuperAdmins <= 1) {
                throw new IllegalStateException("No se puede cambiar el rol del último SuperAdmin");
            }
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioCambioRol);
        nuevoUsuario.setPassword(passwordEncoder.encode(defaultPassword));
        nuevoUsuario.setCambiarPass(true);
        usuarioRepository.save(nuevoUsuario);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        // Auditar cambio de rol
        auditoriaService.registrarAccion(
                null, // No tenemos usuario superadmin aquí
                AccionAuditoria.UPDATE,
                "USUARIO",
                usuario.getId(),
                "Rol cambiado de " + usuario.getRol() + " a " + usuarioCambioRol.getRol() + " para usuario: "
                        + usuario.getEmail(),
                usuario.getRol(),
                usuarioCambioRol.getRol(),
                CategoriaAuditoria.SECURITY,
                SeveridadAuditoria.HIGH);

        return mapToUsuarioDto(nuevoUsuario);
    }

    // Listar usuarios por rol
    public List<UsuarioResponseDto> listarUsuariosPorRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        return usuarioRepository.findByRol(Rol.valueOf(rol)).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // Listar administradores
    public List<UsuarioResponseDto> listarAdministradores() {
        return usuarioRepository.findByRolIn(List.of(Rol.SUPER_ADMIN, Rol.ADMIN)).stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    // Promover a administrador
    public UsuarioResponseDto promoverAAdmin(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() == Rol.ADMIN) {
            throw new IllegalStateException("El usuario ya tiene permisos de administración");
        }

        UsuarioRequestDto adminDto = new UsuarioRequestDto();
        adminDto.setNombre(usuario.getNombre());
        adminDto.setApellido(usuario.getApellido());
        adminDto.setEmail(usuario.getEmail());
        adminDto.setRol(Rol.ADMIN);

        return cambiarRolUsuario(id, adminDto);
    }

    // Degradar administrador
    public UsuarioResponseDto degradarAdmin(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() != Rol.ADMIN) {
            throw new IllegalStateException("Solo se pueden degradar usuarios con rol ADMIN");
        }

        UsuarioRequestDto trabajadorDto = new UsuarioRequestDto();
        trabajadorDto.setNombre(usuario.getNombre());
        trabajadorDto.setApellido(usuario.getApellido());
        trabajadorDto.setEmail(usuario.getEmail());
        trabajadorDto.setRol(Rol.TRABAJADOR);

        return cambiarRolUsuario(id, trabajadorDto);
    }

    // Listar todos los tickets
    public List<TicketResponseDto> listarTodosLosTickets() {
        return ticketRepository.findAll().stream()
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

    // Eliminar ticket
    public void eliminarTicket(int id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
        ticketRepository.delete(ticket);
    }

    // Obtener estadísticas de usuarios
    public Map<String, Object> obtenerEstadisticasUsuarios() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("usuariosActivos", usuarioRepository.countByActivoTrue());
        stats.put("usuariosBloqueados", usuarioRepository.countByBloqueadoTrue());
        stats.put("superAdmins", usuarioRepository.countByRol(Rol.SUPER_ADMIN));
        stats.put("admins", usuarioRepository.countByRol(Rol.ADMIN));
        stats.put("tecnicos", usuarioRepository.countByRol(Rol.TECNICO));
        stats.put("trabajadores", usuarioRepository.countByRol(Rol.TRABAJADOR));
        return stats;
    }

    // Obtener estadísticas de tickets
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

    // Obtener estadísticas del sistema
    public Map<String, Object> obtenerEstadisticasSistema() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usuarios", obtenerEstadisticasUsuarios());
        stats.put("tickets", obtenerEstadisticasTickets());
        stats.put("tecnicosBloqueados", tecnicoRepository.countByBloqueadoTrue());
        return stats;
    }

    // Crear SuperAdmin
    public SuperAdmin crearSuperAdmin(String nombre, String apellido, String email) {
        return new SuperAdmin(nombre, apellido, email);
    }

    // Crear Admin
    public Admin crearAdmin(String nombre, String apellido, String email) {
        return new Admin(nombre, apellido, email);
    }

    // Crear Técnico
    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        return new Tecnico(nombre, apellido, email);
    }

    // Crear Trabajador
    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        return new Trabajador(nombre, apellido, email);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    // Validar datos del usuario
    private void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null ||
                usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    // Validar rol
    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }

    // Crear usuario por rol
    private Usuario crearUsuarioPorRol(UsuarioRequestDto usuarioDto) {
        switch (usuarioDto.getRol()) {
            case SUPER_ADMIN:
                return crearSuperAdmin(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case ADMIN:
                return crearAdmin(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case TECNICO:
                return crearTecnico(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case TRABAJADOR:
                return crearTrabajador(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + usuarioDto.getRol());
        }
    }

    // Método auxiliar para mapear Usuario a DTO
    private UsuarioResponseDto mapToUsuarioDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isCambiarPass(),
                usuario.isActivo(),
                usuario.isBloqueado());
    }

    // Método auxiliar para mapear Ticket a DTO
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
