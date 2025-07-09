package com.poo.miapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EditarUsuarioDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    // Agrega otros campos editables si es necesario

    // Getters y setters
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

    public Object getRol() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRol'");
    }

    public Object getPassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }
}