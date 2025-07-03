package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Buscar usuario por ID
    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Buscar por ID y contraseña (para login)
    public Usuario login(int id, String password) {
        return usuarioRepository.findByIdAndPassword(id, password).orElse(null);
    }

    // Cambiar contraseña (por el mismo usuario)
    public void cambiarPassword(int id, String actual, String nueva) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        if (!usuario.verificarPassword(actual)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
        }
        usuario.cambiarPassword(nueva);
        usuarioRepository.save(usuario);
    }

    // Blanquear contraseña (solo admin debería hacer esto)
    public void blanquearPassword(int idUsuario) {
        Usuario usuario = buscarPorId(idUsuario);
        if (usuario != null) {
            usuario.reiniciarPassword();
            usuarioRepository.save(usuario);
        }
    }

    // Buscar por nombre (opcional)
    public Usuario buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre).orElse(null);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Obtener solo técnicos
    public List<Tecnico> obtenerTecnicos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Tecnico)
                .map(u -> (Tecnico) u)
                .collect(Collectors.toList());
    }

    // Obtener solo trabajadores
    public List<Trabajador> obtenerTrabajadores() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Trabajador)
                .map(u -> (Trabajador) u)
                .collect(Collectors.toList());
    }

    // Obtener administradores (si querés mostrar un listado)
    public List<Admin> obtenerAdministradores() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Admin)
                .map(u -> (Admin) u)
                .collect(Collectors.toList());
    }
}
