package com.poo.miapi.model.notificacion;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificacion_usuario")
public class NotificacionUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int notificacionId;
    private int usuarioId;
    private boolean leida;
    private LocalDateTime fechaLectura;

    public NotificacionUsuario() {
        this.leida = false;
    }

    public NotificacionUsuario(int notificacionId, int usuarioId) {
        this.notificacionId = notificacionId;
        this.usuarioId = usuarioId;
        this.leida = false;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotificacionId() {
        return this.notificacionId;
    }

    public void setNotificacionId(int notificacionId) {
        this.notificacionId = notificacionId;
    }

    public int getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public boolean isLeida() {
        return this.leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaLectura() {
        return this.fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }
}
