package com.poo.miapi.model.core;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends Usuario {

    public SuperAdmin() {
        super();
        this.setRol("SUPER_ADMIN");
    }

    public SuperAdmin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol("SUPER_ADMIN");
    }

    public SuperAdmin(String nombre, String apellido, String email, String password, boolean activo) {
        super(nombre, apellido, email, password, "SUPER_ADMIN", activo);
        this.setRol("SUPER_ADMIN");
    }

    @Override
    public String getTipoUsuario() {
        return "SUPER_ADMIN";
    }
}
