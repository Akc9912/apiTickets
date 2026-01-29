package com.poo.miapi.model.enums;

public enum Rol {
    SUPER_ADMIN("Super Administrador"),
    ADMIN("Administrador"),
    TECNICO("Técnico"),
    TRABAJADOR("Trabajador");

    private final String displayName;

    Rol(String displayName) {
        this.displayName = displayName;
    }

    // Nombre estándar para el front (mayúsculas, igual al nombre del enum)
    public String getName() {
        return name();
    }

    // Nombre legible para mostrar en UI
    public String getDisplayName() {
        return displayName;
    }

    // Valor original del enum (para compatibilidad)
    public String getValue() {
        return name();
    }

    @Override
    public String toString() {
        return name();
    }

    // Verifica si el rol tiene permisos de administración
    public boolean isAdminRole() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    // Verifica si es SuperAdmin
    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    // Verifica si es Administrador
    public boolean isAdmin() {
        return this == ADMIN;
    }

    // Verifica si puede gestionar usuarios
    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    // Verifica si puede gestionar tickets
    public boolean canManageTickets() {
        return this != TRABAJADOR;
    }
}
