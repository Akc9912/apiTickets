package com.poo.miapi.module.user.enums;

public enum UserRole {
    SUPERADMIN("Super Admin"),
    ADMIN("Admin"),
    SUPPORT("Support"),
    DEVELOPER("Developer");

    private final String displayName;

    UserRole(String displayName) {
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
        return this == SUPERADMIN || this == ADMIN;
    }

    // Verifica si es SuperAdmin
    public boolean isSuperAdmin() {
        return this == SUPERADMIN;
    }

    // Verifica si es Administrador
    public boolean isAdmin() {
        return this == ADMIN;
    }

    // Verifica si puede gestionar usuarios
    public boolean canManageUsers() {
        return this == SUPERADMIN || this == ADMIN;
    }

    // Verifica si puede gestionar tickets
    public boolean canManageTickets() {
        return this != SUPPORT;
    }

}