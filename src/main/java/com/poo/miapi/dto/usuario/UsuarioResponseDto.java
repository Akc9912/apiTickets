package com.poo.miapi.dto.usuario;

import com.poo.miapi.model.enums.Rol;

public class UsuarioResponseDto {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;

    // Constructor por defecto requerido por JPA
    public UsuarioResponseDto() {
    }

    // Constructor para crear un DTO a partir de un usuario
    public UsuarioResponseDto(int id, String nombre, String apellido, String email, Rol rol, boolean cambiarPass, boolean activo,
            boolean bloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.cambiarPass = cambiarPass;
        this.activo = activo;
        this.bloqueado = bloqueado;
    }

    // Constructor helper para retrocompatibilidad con String
    public UsuarioResponseDto(int id, String nombre, String apellido, String email, String rolString, boolean cambiarPass, boolean activo,
            boolean bloqueado) {
        this(id, nombre, apellido, email, rolString != null ? Rol.fromString(rolString) : null, cambiarPass, activo, bloqueado);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Método helper para retrocompatibilidad con String
     */
    public void setRol(String rolString) {
        this.rol = rolString != null ? Rol.fromString(rolString) : null;
    }

    /**
     * Método helper para obtener el rol como String
     */
    public String getRolAsString() {
        return rol != null ? rol.name() : null;
    }

    public boolean isCambiarPass() {
        return cambiarPass;
    }

    public void setCambiarPass(boolean cambiarPass) {
        this.cambiarPass = cambiarPass;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}