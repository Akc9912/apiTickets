package com.poo.miapi.model.enums;

public enum CategoriaNotificacion {
    TICKETS("Tickets"),
    USUARIOS("Usuarios"),
    SEGURIDAD("Seguridad"),
    SISTEMA("Sistema"),
    RECORDATORIOS("Recordatorios");

    private final String descripcion;

    CategoriaNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
