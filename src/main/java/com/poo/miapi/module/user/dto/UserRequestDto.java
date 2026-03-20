package com.poo.miapi.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.poo.miapi.module.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "User data for creation or update")
public class UserRequestDto {
    @Schema(description = "User first name", example = "John", required = true)
    @NotNull
    private String name;

    @Schema(description = "User last name", example = "Doe", required = true)
    @NotNull
    private String lastName;

    @Schema(description = "User email address", example = "john.doe@tickets.com", required = true)
    @NotNull
    @Email
    private String email;

    @Schema(description = "User role in the system", example = "DEVELOPER")
    private UserRole role;

    public UserRequestDto() {
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
}
