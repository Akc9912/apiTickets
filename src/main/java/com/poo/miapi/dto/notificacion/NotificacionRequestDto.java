package com.poo.miapi.dto.notificacion;

import java.util.Map;

public class NotificacionRequestDto {
    private String titulo;
    private String mensaje;
    private String tipo;
    private String origenTipo;
    private int origenId;
    private String metadata; // información extra opcional

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

    public void setOtigenTipo(String origenTipo) {
        this.origenTipo = origenTipo;
    }

    public int getOrigenId() {
        return this.origenId;
    }

    public void setOrigenId(int origenId) {
        this.origenId = origenId;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
