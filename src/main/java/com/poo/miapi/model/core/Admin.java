package com.poo.miapi.model.core;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario {

    public Admin() {
        super();
        this.setRol(Rol.ADMIN);
    }

    public Admin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol(Rol.ADMIN);
    }

    @Override
    public String getTipoUsuario() {
        return "ADMIN";
    }
}