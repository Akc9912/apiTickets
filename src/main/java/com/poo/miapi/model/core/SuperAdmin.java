package com.poo.miapi.model.core;
import com.poo.miapi.model.enums.Rol;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPERADMIN")
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
