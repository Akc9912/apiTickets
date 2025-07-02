package com.poo.miapi.service;

import com.poo.miapi.model.Usuario;
import com.poo.miapi.model.Tecnico;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestorDeUsuarios {
    private final List<Usuario> usuarios = new ArrayList<>();

    public void agregarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public Usuario buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Usuario login(int id, String password) {
        Usuario u = buscarPorId(id);
        if (u != null && u.verificarPassword(password) && !esTecnicoBloqueado(u)) {
            return u;
        }
        return null;
    }

    private boolean esTecnicoBloqueado(Usuario u) {
        return (u instanceof Tecnico) && ((Tecnico) u).estaBloqueado();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
