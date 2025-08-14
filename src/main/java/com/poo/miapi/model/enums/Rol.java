package com.poo.miapi.model.enums;

import java.util.Arrays;

/**
 * Enum que define los roles de usuario del sistema
 */
public enum Rol {
    SUPER_ADMIN("Super Administrador"),
    ADMIN("Administrador"),
    TECNICO("Técnico"),
    TRABAJADOR("Trabajador");

    private final String displayName;

    Rol(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
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


    // Obtiene el rol por su nombre (case-insensitive)
    public static Rol fromString(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        try {
            return Rol.valueOf(roleName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + roleName + ". Roles válidos: " + Arrays.toString(values()));
        }
    }

    // Método auxiliar para convertir desde texto legible
    public static Rol fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        for (Rol rol : values()) {
            if (rol.displayName.equalsIgnoreCase(displayName.trim())) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + displayName + ". Roles disponibles: " + Arrays.toString(getAllDisplayNames()));
    }

    // Obtiene todos los nombres para mostrar (útil para frontend)
    public static String[] getAllDisplayNames() {
        return Arrays.stream(values())
                .map(Rol::getDisplayName)
                .toArray(String[]::new);
    }

    // Método auxiliar para obtener el nombre del enum
    public String getName() {
        return name();
    }
}
