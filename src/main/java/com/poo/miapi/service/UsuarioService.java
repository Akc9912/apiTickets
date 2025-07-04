package com.poo.miapi.service;

import com.poo.miapi.model.core.*;
import com.poo.miapi.repository.UsuarioRepository;
import com.poo.miapi.repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Autenticación ---

    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return usuario;
    }

    public boolean verificarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .isPresent();
    }

    // --- Creación de usuarios ---

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

    // --- Contraseña ---

    public void cambiarPassword(int userId, String nuevaPassword) {
        Usuario usuario = buscarPorId(userId);
        if (passwordEncoder.matches(nuevaPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    public void reiniciarPassword(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }

    // --- Bloqueo / activación ---

    public void bloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    public void desbloquearUsuario(int userId) {
        Usuario usuario = buscarPorId(userId);
        usuario.setBloqueado(false);

        if (usuario instanceof Tecnico tecnico) {
            tecnico.reiniciarFallasYMarcas();
        }

        usuarioRepository.save(usuario);
    }

    // --- Consultas ---

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

    // --- Edición ---

    public Usuario actualizarDatos(int id, String nuevoNombre, String nuevoApellido, String nuevoEmail) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(nuevoNombre);
        usuario.setApellido(nuevoApellido);
        usuario.setEmail(nuevoEmail);
        return usuarioRepository.save(usuario);
    }

    // --- Eliminación lógica ---

    public void eliminarUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // --- Estadísticas ---

    public long contarUsuariosTotales() {
        return usuarioRepository.count();
    }

    public long contarUsuariosActivos() {
        return usuarioRepository.countByActivoTrue();
    }

    public long contarTecnicosBloqueados() {
        return tecnicoRepository.countByBloqueadoTrue();
    }

    // --- Utilidad ---

    public String getTipoUsuario(int id) {
        Usuario u = buscarPorId(id);
        return u.getTipoUsuario();
    }
}
