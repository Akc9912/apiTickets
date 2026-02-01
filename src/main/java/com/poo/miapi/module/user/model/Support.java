package com.poo.miapi.module.user.model;

import jakarta.persistence.*;

import com.poo.miapi.module.user.enums.UserRole;

@Entity
@Table(name = "trabajador")
@DiscriminatorValue("TRABAJADOR")
public class Support extends User {

    public Support() {
        super();
        this.setRole(UserRole.SUPPORT);
    }

    public Support(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRole(UserRole.SUPPORT);
    }

    @Override
    public String getUserType() {
        return "SUPPORT";
    }
}