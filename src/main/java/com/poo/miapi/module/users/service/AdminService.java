package com.poo.miapi.module.users.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.users.dto.AdminResponseDto;
import com.poo.miapi.module.users.dto.UserRequestDto;
import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.model.Admin;
import com.poo.miapi.module.users.repository.AdminRepository;
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
