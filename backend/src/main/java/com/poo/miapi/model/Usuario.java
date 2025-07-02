package com.poo.miapi.model;

import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@Entity
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String password;
    private boolean cambiarPass;

    public Usuario() {
        // Requerido por JPA
    }

    public Usuario(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        this.nombre = nombre;
        this.password = null; // se define externamente (ej. en post-constructor o por servicio)
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

    // Métodos funcionales

    public boolean verificarPassword(String intento) {
        return intento != null && this.password.equals(intento);
    }

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

    public void reiniciarPassword() {
        this.password = String.valueOf(this.id);
        this.cambiarPass = true;
    }

    public abstract String getTipoUsuario();

    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nombre + " (ID: " + id + ")";
    }
}
