package com.poo.miapi.model.enums;

public enum TipoNotificacion {
    TICKET_ASIGNADO("Ticket Asignado"),
    TICKET_RESUELTO("Ticket Resuelto"),
    TICKET_REABIERTO("Ticket Reabierto"),
    TICKET_FINALIZADO("Ticket Finalizado"),
    MARCA_ASIGNADA("Marca Asignada"),
    FALLA_REGISTRADA("Falla Registrada"),
    USUARIO_BLOQUEADO("Usuario Bloqueado"),
    PASSWORD_RESET("Reseteo de Contraseña"),
    VALIDACION_REQUERIDA("Validación Requerida"),
    SISTEMA_MANTENIMIENTO("Mantenimiento del Sistema"),
    RECORDATORIO("Recordatorio"),
    ALERTA_SEGURIDAD("Alerta de Seguridad"),
    INFO_GENERAL("Información General");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
