package com.poo.miapi.service;

import com.poo.miapi.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class authService {

    @Autowired
    private gestorDeUsuarios gestorUsuarios;

    /**
     * Devuelve el usuario si las credenciales son válidas, o null si fallan.
     */
    public Usuario login(int id, String password) {
        Usuario usuario = gestorUsuarios.buscarPorId(id);
        if (usuario != null && usuario.verificarPassword(password)) {
            return usuario;
        }
        return null;
    }

    public boolean requiereCambioPassword(Usuario usuario) {
        return usuario.getCambiarPass();
    }
}
