package com.poo.miapi.dto.usuario;

import com.poo.miapi.model.core.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequestDto {
    @NotNull
    private String nombre;
    @NotNull
    private String apellido;
    @NotNull
    @Email
    private String email;
    private Rol rol;

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
}
