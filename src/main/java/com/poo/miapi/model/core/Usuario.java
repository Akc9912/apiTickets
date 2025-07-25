package com.poo.miapi.model.core;

import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@Entity
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String email;

    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;

    // Constructor vacío requerido por JPA
    public Usuario() {
    }

    // Constructor completo para subclases (sin rol, activo ni password como
    // parámetro)
    public Usuario(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = null;
        this.rol = null; // Se asigna en la subclase
        this.cambiarPass = true;
        this.activo = true;
        this.bloqueado = false;
    }

    // Getters
    public Long getId() {
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

    public Rol getRol() {
        return this.rol;
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

    public void setRol(Rol unRol) {
        this.rol = unRol;
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

    // Método abstracto para tipo de usuario
    public abstract String getTipoUsuario();

    // Sobreescritura de métodos
    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nombre + " (ID: " + id + ")";
    }
}