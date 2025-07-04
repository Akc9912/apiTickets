package com.poo.miapi.model.core;

import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@Entity
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;

    // constructor vacio requerido por JPA
    public Usuario() {
    }

    // constructor para uso interno del service
    public Usuario(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = null; // se define por servicio
        this.cambiarPass = true;
        this.activo = true;
        this.bloqueado = false;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isCambiarPass() {
        return this.cambiarPass;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public boolean isBloqueado() {
        return this.bloqueado;
    }

    // Setters
    public void setNombre(String unNombre) {
        this.nombre = unNombre;
    }

    public void setApellido(String unApellido) {
        this.apellido = unApellido;
    }

    public void setEmail(String unEmail) {
        this.email = unEmail;
    }

    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public void setCambiarPass(boolean valor) {
        this.cambiarPass = valor;
    }

    public void setActivo(boolean valor) {
        this.activo = valor;
    }

    public void setBloqueado(boolean valor) {
        this.bloqueado = valor;
    }

    // metodos abstractos
    public abstract String getTipoUsuario();

    // sobreescritura de metodos
    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nombre + " (ID: " + id + ")";
    }
}