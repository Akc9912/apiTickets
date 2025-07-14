package com.poo.miapi.service.core;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TecnicoRepository tecnicoRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TecnicoService tecnicoService;

    UsuarioService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // =========================
    // AUTENTICACIÓN Y LOGIN
    // =========================

    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario);

        return new LoginResponseDto(token, mapToUsuarioDto(usuario));
    }

    // =========================
    // CONTRASEÑAS
    // =========================

    public String cambiarPassword(ChangePasswordDto dto) {
        Usuario usuario = buscarPorId(dto.getUserId());
        String nuevaPass = dto.getNewPassword();

        if (nuevaPass == null || nuevaPass.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (passwordEncoder.matches(nuevaPass, usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPass));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);

        return "Contraseña actualizada correctamente";
    }

    public void reiniciarPassword(ResetPasswordDto dto) {
        Usuario usuario = buscarPorId(dto.getUserId());
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    // =========================
    // ESTADO DEL USUARIO
    // =========================

    public UsuarioResponseDto bloquearUsuario(Long userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto desbloquearUsuario(Long userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(false);

        if (usuario instanceof Tecnico tecnico) {
            tecnicoService.reiniciarFallasYMarcas(tecnico);
        }

        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto bajaLogicaUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    public UsuarioResponseDto altaLogicaUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return mapToUsuarioDto(usuario);
    }

    // =========================
    // CONSULTAS Y LECTURA
    // =========================

    public Usuario buscarPorId(Long id) {
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

    public UsuarioResponseDto actualizarDatos(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(usuario);
    }

    public String getTipoUsuario(Long id) {
        return buscarPorId(id).getTipoUsuario();
    }

    public UsuarioResponseDto obtenerDatos(Long id) {
        Usuario usuario = buscarPorId(id);
        return mapToUsuarioDto(usuario);
    }

    // Editar datos del usuario
    public UsuarioResponseDto editarDatosUsuario(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        return mapToUsuarioDto(usuario);
    }

    // Ver mis tickets (como trabajador o técnico).
    public List<TicketResponseDto> verMisTickets(Long userId) {
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

    // Ver mis notificaciones (todos los usuarios)
    public List<NotificacionResponseDto> verMisNotificaciones(Long userId) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificaciones(userId);
        return notificaciones.stream()
                .map(n -> new NotificacionResponseDto(
                        n.getId(),
                        n.getUsuario().getId(),
                        n.getMensaje(),
                        n.getFechaCreacion()))
                .toList();
    }

    // =========================
    // ESTADÍSTICAS
    // =========================

    public long contarUsuariosTotales() {
        return usuarioRepository.count();
    }

    public long contarUsuariosActivos() {
        return usuarioRepository.countByActivoTrue();
    }

    public long contarTecnicosBloqueados() {
        return tecnicoRepository.countByBloqueadoTrue();
    }

    // =========================
    // MÉTODO AUXILIAR PARA MAPEAR USUARIO A DTO
    // =========================

    private UsuarioResponseDto mapToUsuarioDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.isActivo());
    }
}
