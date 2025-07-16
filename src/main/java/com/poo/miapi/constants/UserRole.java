package com.poo.miapi.constants;

/**
 * Constantes para los roles de usuario del sistema
 */
public final class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String TECNICO = "TECNICO";
    public static final String TRABAJADOR = "TRABAJADOR";

    private UserRole() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Valida si el rol proporcionado es válido
     */
    public static boolean isValidRole(String rol) {
        return ADMIN.equalsIgnoreCase(rol) ||
                TECNICO.equalsIgnoreCase(rol) ||
                TRABAJADOR.equalsIgnoreCase(rol);
    }
}
