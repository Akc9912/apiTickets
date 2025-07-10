package com.poo.miapi.service;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuario.UsuarioRequestDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.repository.UsuarioRepository;
import com.poo.miapi.util.JwtUtil;
import com.poo.miapi.repository.TecnicoRepository;
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

    public Usuario login(String email, String password) {
        Usuario usuario = buscarPorEmail(email);

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return usuario;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(usuario);

        return new LoginResponseDto(token, new UsuarioResponseDto(
                usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), usuario.getTipoUsuario()));
    }

    public boolean verificarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .isPresent();
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

    public void bloquearUsuario(Long userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    public void desbloquearUsuario(Long userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(false);

        if (usuario instanceof Tecnico tecnico) {
            tecnicoService.reiniciarFallasYMarcas(tecnico);
        }

        usuarioRepository.save(usuario);
    }

    public void bajaLogicaUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void altaLogicaUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
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

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Tecnico> listarTecnicosBloqueados() {
        return tecnicoRepository.findByBloqueadoTrue();
    }

    public Usuario actualizarDatos(Long id, String nuevoNombre, String nuevoApellido, String nuevoEmail) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(nuevoNombre);
        usuario.setApellido(nuevoApellido);
        usuario.setEmail(nuevoEmail);
        return usuarioRepository.save(usuario);
    }

    public String getTipoUsuario(Long id) {
        return buscarPorId(id).getTipoUsuario();
    }

    public UsuarioResponseDto obtenerDatos(Long id) {
        Usuario usuario = buscarPorId(id);
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipoUsuario());

    }

    // Editar datos del usuario

    public UsuarioResponseDto editarDatosUsuario(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipoUsuario());
    }

    // Ver mis tickets (como trabajador o técnico).

    public List<Ticket> verMisTickets(Long userId) {
        Usuario usuario = buscarPorId(userId);
        if (usuario instanceof Trabajador trabajador) {
            return trabajador.getMisTickets();
        } else if (usuario instanceof Tecnico tecnico) {
            return tecnico.getMisTickets();
        } else {
            throw new IllegalArgumentException("El usuario no tiene tickets asociados");
        }
    }

    // Ver mis notificaciones (todos los usuarios)

    public Optional<Notificacion> verMisNotificaciones(Long userId) {
        return notificacionService.obtenerNotificaciones(userId);
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
}
