package com.poo.miapi.module.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.model.Developer;
import com.poo.miapi.module.user.model.Superadmin;
import com.poo.miapi.module.user.model.Support;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.repository.UserRepository;
import com.poo.miapi.shared.exception.ResourceNotFoundException;
import com.poo.miapi.shared.util.PasswordHelper;

@Service
public class SuperadminService {

    private final UserRepository userRepository;
    private final AdminService adminService;
    private final DeveloperService developerService;
    private final SupportService supportService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-password}")
    private String defaultPassword;

    public SuperadminService(
            UserRepository userRepository,
            AdminService adminService,
            DeveloperService developerService,
            SupportService supportService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.developerService = developerService;
        this.supportService = supportService;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODOS PÚBLICOS
    // Crear nuevo usuario (solo SuperAdmin)
    public UserResponseDto createUser(UserRequestDto userDto) {
        validateUserData(userDto);

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // Si el rol es SUPERADMIN, asegurarse de que no exista otro
        if (userDto.getRole() == UserRole.SUPERADMIN) {
            long superAdmins = userRepository.countByRole(UserRole.SUPERADMIN);
            if (superAdmins > 0) {
                throw new IllegalStateException("Ya existe un SuperAdmin en el sistema. No se puede crear otro.");
            }
        }

        User newUser = createUserByRole(userDto);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(userDto.getLastName());
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setChangePassword(true);
        newUser.setRole(userDto.getRole());
        userRepository.save(newUser);
        return mapToUserDto(newUser);
    }

    // Listar todos los usuarios
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Ver usuario por ID
    public UserResponseDto getUserById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
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

    // Listar usuarios activos
    public List<UserResponseDto> findActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Listar usuarios bloqueados
    public List<UserResponseDto> findBlockedUsers() {
        return userRepository.findByBlockedTrue().stream()
                .map(this::mapToUserDto)
                .toList();
    }

    // Activar usuario
    public UserResponseDto activateUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setActive(true);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Desactivar usuario
    public UserResponseDto deactivateUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole() == UserRole.SUPERADMIN && user.isActive()) {
            long superAdminsActivos = userRepository.countByRoleAndActiveTrue(UserRole.SUPERADMIN);
            if (superAdminsActivos <= 1) {
                throw new IllegalStateException("No se puede desactivar el último SuperAdmin activo");
            }
        }

        user.setActive(false);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    // Eliminar usuario
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole() == UserRole.SUPERADMIN) {
            long totalSuperAdmins = userRepository.countByRole(UserRole.SUPERADMIN);
            if (totalSuperAdmins <= 1) {
                throw new IllegalStateException("No se puede eliminar el último SuperAdmin");
            }
        }

        userRepository.delete(user);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    private void validateUserData(UserRequestDto userDto) {
        if (userDto.getName() == null || userDto.getLastName() == null ||
                userDto.getEmail() == null || userDto.getRole() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
    }

    private User createUserByRole(UserRequestDto userDto) {
        switch (userDto.getRole()) {
            case SUPERADMIN:
                return new Superadmin(userDto.getName(), userDto.getLastName(), userDto.getEmail());
            case ADMIN:
                return new Admin(userDto.getName(), userDto.getLastName(), userDto.getEmail());
            case DEVELOPER:
                return new Developer(userDto.getName(), userDto.getLastName(), userDto.getEmail());
            case SUPPORT:
                return new Support(userDto.getName(), userDto.getLastName(), userDto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + userDto.getRole());
        }
    }

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
