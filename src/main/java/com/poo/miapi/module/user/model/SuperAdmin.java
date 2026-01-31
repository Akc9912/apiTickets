package com.poo.miapi.module.user.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "super_admin")
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends Usuario {

    public SuperAdmin() {
        super();
        this.setRol(Rol.SUPER_ADMIN);
    }

    public SuperAdmin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol(Rol.SUPER_ADMIN);
    }

    @Override
    public String getTipoUsuario() {
        return "SUPERADMIN";
    }
}
