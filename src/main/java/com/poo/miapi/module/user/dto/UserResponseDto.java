package com.poo.miapi.module.user.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poo.miapi.module.user.enums.UserRole;

@JsonPropertyOrder({ "id", "name", "lastName", "email", "role", "changePassword", "active", "blocked" })
public class UserResponseDto {
    private int id;
    private boolean active;
    private String name;
    private String lastName;
    private String email;
    private UserRole role;
    private boolean changePassword;
    private boolean blocked;

    // Constructor por defecto requerido por JPA
    public UserResponseDto() {
    }

    // Constructor para crear un DTO a partir de un usuario
    public UserResponseDto(int id, String name, String lastName, String email, UserRole role, boolean changePassword,
            boolean active,
            boolean blocked) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.changePassword = changePassword;
        this.active = active;
        this.blocked = blocked;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}