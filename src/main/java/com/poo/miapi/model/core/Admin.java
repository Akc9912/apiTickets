package com.poo.miapi.model.core;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario {

    public Admin() {
        super();
    }

    public Admin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
    }

    @Override
    public String getTipoUsuario() {
        return "ADMIN";
    }
}