package com.poo.miapi.model.enums;

public enum SeveridadNotificacion {
    INFO("Información"),
    WARNING("Advertencia"),
    ERROR("Error"),
    CRITICAL("Crítico");

    private final String descripcion;

    SeveridadNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
