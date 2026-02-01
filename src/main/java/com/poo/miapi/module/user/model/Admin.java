package com.poo.miapi.module.user.model;

import com.poo.miapi.module.user.enums.UserRole;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
        super();
        this.setRole(UserRole.ADMIN);
    }

    public Admin(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRole(UserRole.ADMIN);
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }
}