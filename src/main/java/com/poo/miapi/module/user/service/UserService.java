package com.poo.miapi.module.user.service;

import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.poo.miapi.shared.util.PasswordHelper;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-password}")
    private String defaultPassword;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODOS PÚBLICOS
    // Buscar usuario por ID
    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    // Buscar usuario por email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    // Listar todos los usuarios
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Listar usuarios activos
    public List<UserResponseDto> findActive() {
        return userRepository.findByActiveTrue().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Listar usuarios bloqueados
    public List<UserResponseDto> findBlocked() {
        return userRepository.findByBlockedTrue().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Obtener datos del usuario
    public UserResponseDto getDetails(int userId) {
        User user = findById(userId);
        return mapToUserDto(user);
    }

    // Editar datos del usuario
    public UserResponseDto updateUserData(int userId, UserRequestDto userDto) {
        User user = findById(userId);
        user.setName(userDto.getName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        userRepository.save(user);

        return mapToUserDto(user);
    }

    // Cambiar rol del usuario
    public UserResponseDto updateUserRole(int userId, UserRequestDto userDto) {
        if (userDto.getRole() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        User user = findById(userId);
        user.setRole(userDto.getRole());
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Cambiar estado activo del usuario
    public UserResponseDto toggleUserActive(int userId) {
        User user = findById(userId);
        boolean newState = !user.isActive();
        user.setActive(newState);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Cambiar estado bloqueado del usuario
    public UserResponseDto toggleUserBlocked(int userId) {
        User user = findById(userId);
        boolean newState = !user.isBlocked();
        user.setBlocked(newState);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Resetear contraseña a la por defecto
    public UserResponseDto resetPassword(int userId) {
        User user = findById(userId);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(user.getLastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setChangePassword(true);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Listar usuarios por rol
    public List<UserResponseDto> findUsersByRole(String roleStr) {
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Rol inválido: " + roleStr);
        }
        return userRepository.findByRole(role).stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Obtener tipo de usuario
    public String getUserType(int userId) {
        return findById(userId).getUserType();
    }

    // Búsqueda por nombre
    public List<UserResponseDto> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Búsqueda por apellido
    public List<UserResponseDto> findByLastName(String lastName) {
        return userRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Guardar usuario
    public User save(User user) {
        return userRepository.save(user);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    private UserResponseDto mapToUserDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isChangePassword(),
                user.isActive(),
                user.isBlocked());
    }
}
