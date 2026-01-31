package com.poo.miapi.shared.events.enums;

public enum PrioridadNotificacion {
    BAJA("Baja"),
    NORMAL("Normal"),
    ALTA("Alta"),
    URGENTE("Urgente");

    private final String descripcion;

    PrioridadNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
