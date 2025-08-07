package com.poo.miapi.model.core;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPERADMIN")
public class SuperAdmin extends Usuario {

    public SuperAdmin() {
        super();
        this.setRol(Rol.SUPERADMIN);
    }

    public SuperAdmin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol(Rol.SUPERADMIN);
    }

    @Override
    public String getTipoUsuario() {
        return "SUPERADMIN";
    }
}
