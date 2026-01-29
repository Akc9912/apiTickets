package com.poo.miapi.model.notificacion;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private String origenTipo;
    private int origenId;
    private String metadate; // json
    private LocalDateTime fechaCreacion;

    public Notificacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Notificacion(String titulo, String mensaje, String tipo, String origenTipo, int origenId, String metadate) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.origenTipo = origenTipo;
        this.origenId = origenId;
        this.metadate = metadate;
        this.fechaCreacion = LocalDateTime.now();
    }

    // getter y setters

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

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOrigenTipo() {
        return this.origenTipo;
    }

    public void setOrigenTipo(String origenTipo) {
        this.origenTipo = origenTipo;
    }

    public int getOrigenId() {
        return this.origenId;
    }

    public void setOrigenId(int origenId) {
        this.origenId = origenId;
    }

    public String getMetadate() {
        return this.metadate;
    }

    public void setMetadate(String metadate) {
        this.metadate = metadate;
    }

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
