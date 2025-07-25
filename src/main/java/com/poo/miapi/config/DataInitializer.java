package com.poo.miapi.config;

import com.poo.miapi.model.core.SuperAdmin;
import com.poo.miapi.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("superadmin@sistema.com")) {
            SuperAdmin superAdmin = new SuperAdmin("Super", "Admin", "superadmin@sistema.com");
            superAdmin.setPassword(passwordEncoder.encode("secret"));
            usuarioRepository.save(superAdmin);
            System.out.println("SuperAdmin creado por defecto.");
        }
    }
}
