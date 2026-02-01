package com.poo.miapi.module.user.service;

import com.poo.miapi.module.user.dto.AdminResponseDto;
import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.repository.AdminRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.poo.miapi.shared.util.PasswordHelper;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-password}")
    private String defaultPassword;

    public AdminService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // MÉTODOS PÚBLICOS
    // Buscar admin por ID
    public Admin findById(int id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));
    }

    // Buscar admin por email
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));
    }

    // Obtener datos del admin
    public AdminResponseDto getDetails(int id) {
        Admin admin = findById(id);
        return mapToAdminDto(admin);
    }

    // Editar datos del admin
    public AdminResponseDto updateAdminData(int adminId, UserRequestDto userDto) {
        Admin admin = findById(adminId);
        admin.setName(userDto.getName());
        admin.setLastName(userDto.getLastName());
        admin.setEmail(userDto.getEmail());
        adminRepository.save(admin);

        return mapToAdminDto(admin);
    }

    // Resetear contraseña a la por defecto
    public AdminResponseDto resetPassword(int adminId) {
        Admin admin = findById(adminId);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(admin.getLastName());
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setChangePassword(true);
        adminRepository.save(admin);
        return mapToAdminDto(admin);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    private AdminResponseDto mapToAdminDto(Admin admin) {
        return new AdminResponseDto(
                admin.getId(),
                admin.getName(),
                admin.getLastName(),
                admin.getEmail(),
                admin.getRole(),
                admin.isChangePassword(),
                admin.isActive(),
                admin.isBlocked());
    }
}
