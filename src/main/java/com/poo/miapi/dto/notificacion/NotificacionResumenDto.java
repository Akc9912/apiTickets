package com.poo.miapi.dto.notificacion;

import java.time.LocalDateTime;

public class NotificacionResumenDto {
    private int id;
    private String titulo;
    private String tipo;
    private boolean leida;
    private LocalDateTime fechaCreacion;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isLeida() {
        return this.leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
