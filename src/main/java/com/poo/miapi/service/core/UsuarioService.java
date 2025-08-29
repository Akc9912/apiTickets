package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.util.PasswordHelper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    

    private final UsuarioRepository usuarioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TecnicoService tecnicoService;

    @Value("${app.default-password}")
    private String defaultPassword;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            TecnicoRepository tecnicoRepository,
            PasswordEncoder passwordEncoder,
            TecnicoService tecnicoService) {
        this.usuarioRepository = usuarioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tecnicoService = tecnicoService;
    }


    // Estado del usuario - cambia de true a false
    public UsuarioResponseDto setUsuarioActivo(int userId) {
    Usuario usuario = buscarPorId(userId);
    usuario.setActivo(!usuario.isActivo());
    usuarioRepository.save(usuario);
    return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto setUsuarioBloqueado(int userId) {
        Usuario usuario = buscarPorId(userId);
        boolean nuevoEstado = !usuario.isBloqueado();
        usuario.setBloqueado(nuevoEstado);
        // Si se está desbloqueando y es técnico, aseguramos que quede activo
        if (nuevoEstado == false && usuario instanceof Tecnico tecnico) {
            usuario.setActivo(true);
            tecnicoService.reiniciarFallasYMarcas(tecnico);
        }
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // Consultas y operaciones sobre usuarios

    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    public List<UsuarioResponseDto> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }

    public List<UsuarioResponseDto> listarTecnicosBloqueados() {
        return tecnicoRepository.findByBloqueadoTrue().stream()
                .map(this::mapToUsuarioDto)
                .toList();
    }


    public String getTipoUsuario(int id) {
        return buscarPorId(id).getTipoUsuario();
    }

    public UsuarioResponseDto obtenerDatos(int id) {
        Usuario usuario = buscarPorId(id);
        return mapToUsuarioDto(usuario);
    }

    // Editar datos del usuario
    public UsuarioResponseDto editarDatosUsuario(int id, UsuarioRequestDto dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(usuario);
    }

    // Ver mis tickets (como trabajador o técnico).
    public List<TicketResponseDto> verMisTickets(int userId) {
        Usuario usuario = buscarPorId(userId);
        List<Ticket> tickets;
        if (usuario instanceof Trabajador trabajador) {
            tickets = trabajador.getMisTickets();
        } else if (usuario instanceof Tecnico tecnico) {
            tickets = tecnico.getTicketsActuales();
        } else {
            throw new IllegalArgumentException("El usuario no tiene tickets asociados");
        }
        return tickets.stream()
                .map(ticket -> new TicketResponseDto(
                        ticket.getId(),
                        ticket.getTitulo(),
                        ticket.getDescripcion(),
                        ticket.getEstado(),
                        ticket.getCreador().getNombre(),
                        ticket.getTecnicoActual() != null ? ticket.getTecnicoActual().getNombre() : null,
                        ticket.getFechaCreacion(),
                        ticket.getFechaUltimaActualizacion()))
                .toList();
    }


    // Metodo auxiliar para mapear Usuario a UsuarioResponseDto
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

    // Crear usuario (usado por crearUsuarioConValidacion)
    private UsuarioResponseDto crearUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        Rol rolEnum = usuarioDto.getRol();
        if (rolEnum == null) {
            throw new IllegalArgumentException("Rol no válido");
        }
        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioDto);
        nuevoUsuario.setRol(rolEnum);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setBloqueado(false);
        nuevoUsuario.setCambiarPass(true);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(usuarioDto.getApellido());
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        usuarioRepository.save(nuevoUsuario);
        return mapToUsuarioDto(nuevoUsuario);
    }

    // Instancia el tipo correcto de usuario según el rol
    private Usuario crearUsuarioPorRol(UsuarioRequestDto dto) {
        switch (dto.getRol()) {
            case SUPER_ADMIN:
                return new SuperAdmin(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case ADMIN:
                return new Admin(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case TECNICO:
                return new Tecnico(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case TRABAJADOR:
                return new Trabajador(dto.getNombre(), dto.getApellido(), dto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + dto.getRol());
        }
    }
    // Creación de usuario con validación de rol del creador
    public UsuarioResponseDto crearUsuarioConValidacion(UsuarioRequestDto usuarioDto, Usuario usuarioCreador) {
        if (usuarioCreador == null || usuarioCreador.getRol() == null) {
            throw new IllegalArgumentException("No autorizado");
        }
        Rol rolCreador = usuarioCreador.getRol();
        Rol rolNuevo = usuarioDto.getRol();
        if (!rolCreador.canManageUsers()) {
            throw new IllegalArgumentException("Solo Admin o SuperAdmin pueden crear usuarios");
        }
        if (rolCreador == Rol.ADMIN && rolNuevo == Rol.SUPER_ADMIN) {
            throw new IllegalArgumentException("Admin no puede crear SuperAdmin");
        }
        return crearUsuario(usuarioDto);
    }   

        // Resetear contraseña a la por defecto
        public UsuarioResponseDto resetearPassword(int userId) {
            Usuario usuario = buscarPorId(userId);
            String rawPassword = PasswordHelper.generarPasswordPorDefecto(usuario.getApellido());
            usuario.setPassword(passwordEncoder.encode(rawPassword));
            usuario.setCambiarPass(true);
            usuarioRepository.save(usuario);
            return mapToUsuarioDto(usuario);
        }

        // Cambiar rol de usuario
        public UsuarioResponseDto cambiarRolUsuario(int userId, UsuarioRequestDto dto) {
            Usuario usuario = buscarPorId(userId);
            Rol nuevoRol = dto.getRol();
            if (nuevoRol == null) {
                throw new IllegalArgumentException("Rol no válido");
            }
            usuario.setRol(nuevoRol);
            usuarioRepository.save(usuario);
            return mapToUsuarioDto(usuario);
        }

        // Listar usuarios por rol
        public List<UsuarioResponseDto> listarUsuariosPorRol(String rolStr) {
            Rol rol;
            try {
                rol = Rol.valueOf(rolStr.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Rol inválido: " + rolStr);
            }
            return usuarioRepository.findByRol(rol).stream()
                    .map(this::mapToUsuarioDto)
                    .toList();
        }

    public java.util.List<UsuarioResponseDto> listarTodosFiltrado(Usuario usuarioAutenticado) {
        if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null) {
            throw new SecurityException("No autorizado");
        }
        Rol rol = usuarioAutenticado.getRol();
        if (rol != Rol.ADMIN && rol != Rol.SUPER_ADMIN) {
            throw new SecurityException("No autorizado");
        }
        return usuarioRepository.findAll().stream()
            .map(usuario -> {
                if (rol == Rol.ADMIN && usuario.getRol() == Rol.SUPER_ADMIN) {
                    return null;
                }
                return mapToUsuarioDto(usuario);
            })
            .filter(java.util.Objects::nonNull)
            .toList();
        }
}
