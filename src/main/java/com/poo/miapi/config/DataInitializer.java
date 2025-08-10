package com.poo.miapi.config;

import com.poo.miapi.model.core.SuperAdmin;
import com.poo.miapi.model.core.Admin;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.repository.core.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor para inyección de dependencias
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            logger.info("[DataInitializer] Inicio de configuración inicial de datos");

            String superAdminEmail = "superadmin@sistema.com";
            String adminEmail = "admin@sistema.com";
            String tecnicoEmail = "tecnico@sistema.com";
            String trabajadorEmail = "trabajador@sistema.com";

            logger.info("[DataInitializer] Verificando si existe SuperAdmin");           
            if (usuarioRepository.countByEmail(superAdminEmail) == 0) {
                logger.info("[DataInitializer] Creando SuperAdmin...");
                crearSuperAdmin(superAdminEmail);
            } else {
                logger.info("[DataInitializer] SuperAdmin ya existe");
            }

            logger.info("[DataInitializer] Verificando si existe Admin");
            if (usuarioRepository.countByEmail(adminEmail) == 0) {
                logger.info("[DataInitializer] Creando Admin...");
                crearAdmin(adminEmail);
            } else {
                logger.info("[DataInitializer] Admin ya existe");
            }

            logger.info("[DataInitializer] Verificando si existe Tecnico");
            if (usuarioRepository.countByEmail(tecnicoEmail) == 0) {
                logger.info("[DataInitializer] Creando Tecnico...");
                crearTecnico(tecnicoEmail);
            } else {
                logger.info("[DataInitializer] Tecnico ya existe");
            }

            logger.info("[DataInitializer] Verificando si existe Trabajador");
            if (usuarioRepository.countByEmail(trabajadorEmail) == 0) {
                logger.info("[DataInitializer] Creando Trabajador...");
                crearTrabajador(trabajadorEmail);
            } else {
                logger.info("[DataInitializer] Trabajador ya existe");
            }

            logger.info("[DataInitializer] Usuarios totales: {}", usuarioRepository.count());
            logger.info("[DataInitializer] Configuración inicial completada");

        } catch (Exception e) {
            logger.error("[DataInitializer] Error: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar datos del sistema", e);
        }
    }
    
    private void crearSuperAdmin(String email) {
        try {
            SuperAdmin superAdmin = new SuperAdmin("Super", "Admin", email);
            superAdmin.setPassword(passwordEncoder.encode("secret"));
            superAdmin.setCambiarPass(false);
            usuarioRepository.save(superAdmin);
            logger.info("[DataInitializer] SuperAdmin creado: {}", email);
        } catch (Exception e) {
            logger.error("[DataInitializer] Error creando SuperAdmin: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void crearAdmin(String email) {
        try {
            Admin admin = new Admin("Admin", "Sistema", email);
            admin.setPassword(passwordEncoder.encode("secret"));
            admin.setCambiarPass(false);
            usuarioRepository.save(admin);
            logger.info("[DataInitializer] Admin creado: {}", email);
        } catch (Exception e) {
            logger.error("[DataInitializer] Error creando Admin: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void crearTecnico(String email) {
        try {
            Tecnico tecnico = new Tecnico("Tecnico", "Sistema", email);
            tecnico.setPassword(passwordEncoder.encode("secret"));
            tecnico.setCambiarPass(false);
            usuarioRepository.save(tecnico);
            logger.info("[DataInitializer] Tecnico creado: {}", email);
        } catch (Exception e) {
            logger.error("[DataInitializer] Error creando Tecnico: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void crearTrabajador(String email) {
        try {
            Trabajador trabajador = new Trabajador("Trabajador", "Sistema", email);
            trabajador.setPassword(passwordEncoder.encode("secret"));
            trabajador.setCambiarPass(false);
            usuarioRepository.save(trabajador);
            logger.info("[DataInitializer] Trabajador creado: {}", email);
        } catch (Exception e) {
            logger.error("[DataInitializer] Error creando Trabajador: {}", e.getMessage(), e);
            throw e;
        }
    }
}
