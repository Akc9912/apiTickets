package com.poo.miapi.model;

import java.lang.String;

public abstract class Usuario {
    private static int contadorIds = 1; // Contador estático para generar IDs únicos

    private int id;
    private String nombre;
    private String password;
    private boolean cambiarPass;

    // Constructor
    public Usuario(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        this.id = contadorIds++;
        this.nombre = nombre;

        // La contraseña inicial es igual al ID → forzar cambio en primer login
        this.password = String.valueOf(this.id);
        this.cambiarPass = true;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getCambiarPass() {
        return this.cambiarPass;
    }

    // Setters
    public void setNombre(String unNombre) {
        if (unNombre == null || unNombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = unNombre;
    }

    /**
     * Método reservado para uso interno (ej. administrador).
     * Modifica directamente la contraseña sin alterar cambiarPass.
     */
    public void setPassword(String unPass) {
        if (unPass == null || unPass.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (unPass.equals(String.valueOf(this.id))) {
            throw new IllegalArgumentException("La contraseña no puede ser igual al ID del usuario.");
        }
        this.password = unPass;
    }

    public void setCambiarPass(boolean valor) {
        this.cambiarPass = valor;
    }

    // --------------------
    // Métodos de utilidad
    // --------------------

    public boolean verificarPassword(String intento) {
        return intento != null && this.password.equals(intento);
    }

    /**
     * Cambia la contraseña del usuario y marca que ya no necesita cambiarla.
     * Se usa cuando el usuario realiza un cambio voluntario desde el sistema.
     */
    public void cambiarPassword(String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        if (nuevaPassword.equals(String.valueOf(this.id))) {
            throw new IllegalArgumentException("No podés usar tu ID como contraseña.");
        }
        if (nuevaPassword.equals(this.password)) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior.");
        }

        this.password = nuevaPassword;
        this.cambiarPass = false;
    }

    /**
     * Reinicia la contraseña al valor del ID.
     * Forzado normalmente por un administrador (blanqueo).
     */
    public void reiniciarPassword() {
        this.password = String.valueOf(this.id);
        this.cambiarPass = true;
    }

    /**
     * Método abstracto para obtener el tipo de usuario (Admin, Técnico,
     * Trabajador).
     */
    public abstract String getTipoUsuario();

    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nombre + " (ID: " + id + ")"; // para usar en depuracion
    }
}
