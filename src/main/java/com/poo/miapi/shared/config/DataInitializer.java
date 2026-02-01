package com.poo.miapi.shared.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.model.Superadmin;
import com.poo.miapi.module.user.model.Developer;
import com.poo.miapi.module.user.model.Support;
import com.poo.miapi.module.user.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            String superadminEmail = "superadmin@sistema.com";
            String adminEmail = "admin@sistema.com";
            String developerEmail = "developer@sistema.com";
            String supportEmail = "support@sistema.com";

            if (userRepository.countByEmail(superadminEmail) == 0) {
                createSuperadmin(superadminEmail);
            }

            if (userRepository.countByEmail(adminEmail) == 0) {
                createAdmin(adminEmail);
            }

            if (userRepository.countByEmail(developerEmail) == 0) {
                createDeveloper(developerEmail);
            }

            if (userRepository.countByEmail(supportEmail) == 0) {
                createSupport(supportEmail);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error initializing system data", e);
        }
    }

    private void createSuperadmin(String email) {
        Superadmin superadmin = new Superadmin("Super", "Admin", email);
        superadmin.setPassword(passwordEncoder.encode("secret"));
        superadmin.setChangePassword(false);
        userRepository.save(superadmin);
    }

    private void createAdmin(String email) {
        Admin admin = new Admin("Admin", "Sistema", email);
        admin.setPassword(passwordEncoder.encode("secret"));
        admin.setChangePassword(false);
        userRepository.save(admin);
    }

    private void createDeveloper(String email) {
        Developer developer = new Developer("Developer", "Sistema", email);
        developer.setPassword(passwordEncoder.encode("secret"));
        developer.setChangePassword(false);
        userRepository.save(developer);
    }

    private void createSupport(String email) {
        Support support = new Support("Support", "Sistema", email);
        support.setPassword(passwordEncoder.encode("secret"));
        support.setChangePassword(false);
        userRepository.save(support);
    }
}
