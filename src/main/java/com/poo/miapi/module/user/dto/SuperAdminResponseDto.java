package com.poo.miapi.module.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.poo.miapi.module.user.enums.UserRole;

@JsonTypeName("SUPERADMIN")
public class SuperadminResponseDto extends UserResponseDto {

    public SuperadminResponseDto() {
        super();
    }

    public SuperadminResponseDto(int id, String name, String lastName, String email, UserRole role,
            boolean changePassword,
            boolean active, boolean blocked) {
        super(id, name, lastName, email, role, changePassword, active, blocked);
    }
    
}
