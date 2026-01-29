package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando un usuario es creado
 */
public class UsuarioCreadoEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final Usuario creadoPor;
    private final String passwordTemporal;

    public UsuarioCreadoEvent(Object source, Usuario usuario, Usuario creadoPor, String passwordTemporal) {
        super(source);
        this.usuario = usuario;
        this.creadoPor = creadoPor;
        this.passwordTemporal = passwordTemporal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public String getPasswordTemporal() {
        return passwordTemporal;
    }
}
