package com.poo.miapi.module.users.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String globalRole;
    private String status;
}
