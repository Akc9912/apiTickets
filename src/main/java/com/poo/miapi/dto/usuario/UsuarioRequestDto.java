package com.poo.miapi.dto.usuario;

import com.poo.miapi.model.core.Rol;

public class UsuarioRequestDto {
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    public UsuarioRequestDto() {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Rol getRolEnum() {
        try {
            return rol != null ? Rol.valueOf(rol) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
