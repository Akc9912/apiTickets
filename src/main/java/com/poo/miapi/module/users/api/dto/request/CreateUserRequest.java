package com.poo.miapi.module.users.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder 
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
