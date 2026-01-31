package com.poo.miapi.model.enums;

public enum AccionAuditoria {
    // Acciones CRUD básicas
    CREATE("Crear"),
    UPDATE("Actualizar"),
    DELETE("Eliminar"),

    // Acciones de autenticación
    LOGIN("Iniciar sesión"),
    LOGOUT("Cerrar sesión"),
    LOGIN_FAILED("Fallo de autenticación"),

    // Acciones de tickets
    ASSIGN_TICKET("Asignar ticket"),
    UNASSIGN_TICKET("Desasignar ticket"),
    RESOLVE_TICKET("Resolver ticket"),
    REOPEN_TICKET("Reabrir ticket"),

    // Acciones de solicitudes de devolución
    APPROVE_RETURN("Aprobar devolución"),
    REJECT_RETURN("Rechazar devolución"),
    REQUEST_RETURN("Solicitar devolución"),

    // Acciones de gestión de usuarios
    BLOCK_USER("Bloquear usuario"),
    UNBLOCK_USER("Desbloquear usuario"),
    ACTIVATE_USER("Activar usuario"),
    DEACTIVATE_USER("Desactivar usuario"),

    // Acciones de contraseñas
    CHANGE_PASSWORD("Cambiar contraseña"),
    RESET_PASSWORD("Reiniciar contraseña"),

    // Otras acciones de negocio
    EVALUATE_TICKET("Evaluar ticket"),
    CREATE_INCIDENT("Crear incidente");

    private final String displayName;

    AccionAuditoria(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
