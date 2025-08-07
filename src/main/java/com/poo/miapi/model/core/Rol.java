package com.poo.miapi.model.core;

/**
 * Enum que define los roles de usuario del sistema
 */
public enum Rol {
    SUPERADMIN("Super Administrador", "Acceso total al sistema"),
    ADMIN("Administrador", "Gestión completa del sistema"),
    TECNICO("Técnico", "Gestión de tickets y resolución de incidentes"),
    TRABAJADOR("Trabajador", "Creación y seguimiento de tickets");

    private final String displayName;
    private final String description;

    Rol(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el rol tiene permisos de administración
     */
    public boolean isAdminRole() {
        return this == SUPERADMIN || this == ADMIN;
    }

    /**
     * Verifica si es SuperAdmin
     */
    public boolean isSuperAdmin() {
        return this == SUPERADMIN;
    }

    /**
     * Verifica si puede gestionar usuarios
     */
    public boolean canManageUsers() {
        return this == SUPERADMIN || this == ADMIN;
    }

    /**
     * Verifica si puede gestionar tickets
     */
    public boolean canManageTickets() {
        return this != TRABAJADOR; // Todos excepto trabajador pueden gestionar tickets
    }

    /**
     * Obtiene el rol por su nombre (case-insensitive)
     */
    public static Rol fromString(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        try {
            return Rol.valueOf(roleName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + roleName + ". Roles válidos: " + java.util.Arrays.toString(values()));
        }
    }
}
