package com.poo.miapi.module.users.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.users.api.dto.request.CreateUserRequest;
import com.poo.miapi.module.users.api.dto.response.UserResponse;
import com.poo.miapi.module.users.dto.UserRequestDto;
import com.poo.miapi.module.users.dto.UserResponseDto;
import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;
import com.poo.miapi.module.users.repository.UserRepository;
import com.poo.miapi.shared.util.PasswordHelper;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Service
@AllArgsConstructor 
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** CREATE */
    public UserResponse create(CreateUserRequest request){
        // buscar si el mail existe en la db (omitimos soft deleted x ahora)

        // creamos el user

        // guardo user en db

        // retorno dto (revisar que retornamos si el dto o un booleano)
    }

    /** READ */

    // buscar por id 

    // buscar por email

    // listar usuarios (depende q   ue rol devuelve soft delete o no)

    // listar por rol

    // listar por estado

    /** UPDATE */

    // actualizar datos user

    /** DELETE */

    // soft delete y cambio de estado
}
