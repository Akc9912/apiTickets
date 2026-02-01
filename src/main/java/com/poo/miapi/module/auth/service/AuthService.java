package com.poo.miapi.module.auth.service;

import com.poo.miapi.module.auth.dto.*;
import com.poo.miapi.module.user.dto.*;
import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.model.Superadmin;
import com.poo.miapi.module.user.model.Developer;
import com.poo.miapi.module.user.model.Support;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autenticar usuario con email y contraseña
     * 
     * @param loginRequest Datos de login (email y contraseña)
     * @return Token JWT y datos del usuario si la autenticación es exitosa
     * @throws EntityNotFoundException  Si el usuario no existe o está inactivo
     * @throws IllegalArgumentException Si la contraseña es incorrecta
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        User user;
        try {
            user = userService.findByEmail(loginRequest.getEmail());
        } catch (EntityNotFoundException e) {
            // Ejecutar una verificación dummy para mantener el mismo tiempo de respuesta
            passwordEncoder.matches(loginRequest.getPassword(), "$2a$10$dummyHashToPreventTimingAttacks");
            throw new EntityNotFoundException("User not found");
        }

        // Verificar la contraseña
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        // Verificación de seguridad: usuarios inactivos
        // Los usuarios bloqueados SÍ pueden iniciar sesión pero no realizar acciones
        if (!user.isActive()) {
            throw new EntityNotFoundException("User not found");
        }

        if (!passwordMatches) {
            throw new IllegalArgumentException("Incorrect password");
        }

        String token = jwtService.generateToken(user);
        UserResponseDto userDto;

        switch (user.getRole().name()) {
            case "DEVELOPER" -> {
                Developer developer = (Developer) user;
                userDto = new DeveloperResponseDto(
                        developer.getId(),
                        developer.getName(),
                        developer.getLastName(),
                        developer.getEmail(),
                        developer.getRole(),
                        developer.isChangePassword(),
                        developer.isActive(),
                        developer.isBlocked(),
                        developer.getFailures(),
                        developer.getWarnings());
            }
            case "SUPPORT" -> {
                Support support = (Support) user;
                userDto = new SupportResponseDto(
                        support.getId(),
                        support.getName(),
                        support.getLastName(),
                        support.getEmail(),
                        support.getRole(),
                        support.isChangePassword(),
                        support.isActive(),
                        support.isBlocked());
            }
            case "ADMIN" -> {
                Admin admin = (Admin) user;
                userDto = new AdminResponseDto(
                        admin.getId(),
                        admin.getName(),
                        admin.getLastName(),
                        admin.getEmail(),
                        admin.getRole(),
                        admin.isChangePassword(),
                        admin.isActive(),
                        admin.isBlocked());
            }
            case "SUPERADMIN" -> {
                Superadmin superadmin = (Superadmin) user;
                userDto = new SuperadminResponseDto(
                        superadmin.getId(),
                        superadmin.getName(),
                        superadmin.getLastName(),
                        superadmin.getEmail(),
                        superadmin.getRole(),
                        superadmin.isChangePassword(),
                        superadmin.isActive(),
                        superadmin.isBlocked());
            }
            default -> {
                userDto = new UserResponseDto(
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
        return new LoginResponseDto(token, userDto);
    }

    /**
     * Cambiar contraseña del usuario
     * 
     * @param dto Datos del cambio de contraseña
     * @throws EntityNotFoundException  Si el usuario no existe
     * @throws IllegalArgumentException Si la contraseña es inválida o igual a la
     *                                  anterior
     */
    public void changePassword(ChangePasswordDto dto) {
        User user = userService.findById(dto.getUserId());

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the current password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setChangePassword(false);
        userService.save(user);
    }

    /**
     * Reiniciar contraseña de usuario (por administrador)
     * Establece la contraseña al ID del usuario y marca para cambio obligatorio
     * 
     * @param dto Datos del reset de contraseña
     * @throws EntityNotFoundException Si el usuario no existe
     */
    public void resetPassword(ResetPasswordDto dto) {
        User user = userService.findById(dto.getUserId());

        user.setPassword(passwordEncoder.encode(String.valueOf(user.getId())));
        user.setChangePassword(true);
        userService.save(user);
    }
}
