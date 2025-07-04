package com.poo.miapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poo.miapi.model.core.Usuario;

@Service
public class AuthService {
    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    public Usuario login(int id, String password) {
        Usuario usuario = gestorUsuarios.buscarPorId(id);
        if (usuario != null && usuario.verificarPassword(password)) {
            return usuario;
        }
        return null;
    }

    public boolean cambiarPassword(int id, String actual, String nueva) {
        Usuario usuario = gestorUsuarios.buscarPorId(id);
        if (usuario == null || !usuario.verificarPassword(actual)) {
            throw new RuntimeException("Credenciales incorrectas.");
        }
        usuario.cambiarPassword(nueva);
        return true;
    }

    public boolean requiereCambioPassword(Usuario usuario) {
        return usuario.getCambiarPass();
    }
}
