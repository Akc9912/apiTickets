package com.poo.miapi.model.notificacion;

import com.poo.miapi.model.core.Usuario;

public class Notificacion {
    int id;
    Usuario usuario;
    String mensaje;

    public Notificacion() {
    }

    public Notificacion(Usuario usuario, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setUsuario(Usuario u) {
        this.usuario = u;
    }

    public void setMensaje(String m) {
        this.mensaje = m;
    }
}
