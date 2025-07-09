package com.poo.miapi.service;

import com.poo.miapi.dto.*;
import com.poo.miapi.model.core.*;
import com.poo.miapi.repository.UsuarioRepository;
import com.poo.miapi.repository.TecnicoRepository;
import com.poo.miapi.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

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

    public void cambiarPassword(CambioPasswordDto dto) {
        Usuario usuario = buscarPorId(dto.getIdUsuario());
        String nuevaPass = dto.getNuevaPassword();

        if (nuevaPass == null || nuevaPass.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (passwordEncoder.matches(nuevaPass, usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPass));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    public void reiniciarPassword(ReinicioPasswordDto dto) {
        Usuario usuario = buscarPorId(dto.getIdUsuario());
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    // =========================
    // ESTADO DEL USUARIO
    // =========================

    public void bloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    public void desbloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(false);

        if (usuario instanceof Tecnico tecnico) {
            tecnicoService.reiniciarFallasYMarcas(tecnico);
        }

        usuarioRepository.save(usuario);
    }

    public void bajaLogicaUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void altaLogicaUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    // =========================
    // CONSULTAS Y LECTURA
    // =========================

    public Usuario buscarPorId(int id) {
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

    public Usuario actualizarDatos(int id, String nuevoNombre, String nuevoApellido, String nuevoEmail) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(nuevoNombre);
        usuario.setApellido(nuevoApellido);
        usuario.setEmail(nuevoEmail);
        return usuarioRepository.save(usuario);
    }

    public String getTipoUsuario(int id) {
        return buscarPorId(id).getTipoUsuario();
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
