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

    // AUTENTICACIÓN Y LOGIN

    // Autentica un usuario por email y contraseña.
    public Usuario login(String email, String password) {
        Usuario usuario = buscarPorEmail(email);

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        } // separar validacione? ver codigos de error

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return usuario;
    }

    // Login con generación de JWT.
    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(usuario);

        return new LoginResponseDto(token, new UsuarioResponseDto(
                usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), usuario.getTipoUsuario()));
    }

    // Verifica si las credenciales ingresadas son válidas.

    public boolean verificarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .isPresent();
    }

    // REGISTRO Y CREACIÓN DE USUARIOS

    // Registra un nuevo usuario de tipo ADMIN, TECNICO o TRABAJADOR.
    public UsuarioResponseDto registrarUsuario(CrearUsuarioDto dto) {
        Usuario nuevo;

        switch (dto.getTipo()) {
            case "ADMIN" -> nuevo = new Admin(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case "TECNICO" -> nuevo = new Tecnico(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case "TRABAJADOR" -> nuevo = new Trabajador(dto.getNombre(), dto.getApellido(), dto.getEmail());
            default -> throw new IllegalArgumentException("Tipo de usuario no válido");
        }

        nuevo.setPassword(passwordEncoder.encode(String.valueOf(nuevo.getId())));
        nuevo.setCambiarPass(true);

        usuarioRepository.save(nuevo);

        return new UsuarioResponseDto(nuevo.getId(), nuevo.getNombre(), nuevo.getApellido(),
                nuevo.getEmail(), nuevo.getTipoUsuario());
    }

    // Métodos individuales para crear usuarios por tipo
    public Admin crearAdmin(String nombre, String apellido, String email) {
        Admin admin = new Admin(nombre, apellido, email);
        admin.setPassword(passwordEncoder.encode(email));
        return usuarioRepository.save(admin);
    }

    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        Tecnico tecnico = new Tecnico(nombre, apellido, email);
        tecnico.setPassword(passwordEncoder.encode(email));
        return usuarioRepository.save(tecnico);
    }

    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        Trabajador trabajador = new Trabajador(nombre, apellido, email);
        trabajador.setPassword(passwordEncoder.encode(email));
        return usuarioRepository.save(trabajador);
    }

    // CONTRASEÑAS

    // Cambia la contraseña de un usuario.
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

    // Método alternativo para cambiar contraseña por ID directo. (no, implementar
    // DTO)
    public void cambiarPassword(int userId, String nuevaPassword) {
        Usuario usuario = buscarPorId(userId);

        if (passwordEncoder.matches(nuevaPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    // Reinicia la contraseña del usuario a su ID.
    public void reiniciarPassword(ReinicioPasswordDto dto) {
        Usuario usuario = buscarPorId(dto.getIdUsuario());
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    public void reiniciarPassword(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    // ESTADO DEL USUARIO

    // Bloquea al usuario.
    public void bloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    // Desbloquea al usuario. Reinicia datos si es técnico.
    public void desbloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(false);

        if (usuario instanceof Tecnico tecnico) {
            tecnico.reiniciarFallasYMarcas();
        }

        usuarioRepository.save(usuario);
    }

    // Elimina lógicamente un usuario (activo=false).
    public void eliminarUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // CONSULTAS Y LECTURA

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

    // ESTADÍSTICAS

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
