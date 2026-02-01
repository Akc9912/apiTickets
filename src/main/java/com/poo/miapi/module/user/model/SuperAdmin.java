package com.poo.miapi.module.user.model;

import com.poo.miapi.module.user.enums.UserRole;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "superadmin")
@DiscriminatorValue("SUPERADMIN")
public class Superadmin extends User {

    public Superadmin() {
        super();
        this.setRole(UserRole.SUPERADMIN);
    }

    public Superadmin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRole(UserRole.SUPERADMIN);
    }

    @Override
    public String getUserType() {
        return "SUPERADMIN";
    }
}
