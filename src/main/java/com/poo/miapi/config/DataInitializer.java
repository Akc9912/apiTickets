package com.poo.miapi.config;

import com.poo.miapi.model.core.SuperAdmin;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

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
            logger.info("Iniciando configuración inicial de datos...");
            
            // Verificar la conexión a la base de datos
            long totalUsuarios = usuarioRepository.count();
            logger.info("Total de usuarios en la base de datos: {}", totalUsuarios);
            
            // Verificar si el SuperAdmin ya existe
            String superAdminEmail = "superadmin@sistema.com";
            long countSuperAdmin = usuarioRepository.countByEmail(superAdminEmail);
            boolean existeSuperAdmin = countSuperAdmin > 0;
            
            logger.info("Verificando existencia del SuperAdmin:");
            logger.info("   Email a buscar: {}", superAdminEmail);
            logger.info("   Cantidad encontrada: {}", countSuperAdmin);
            logger.info("   ¿Ya existe?: {}", existeSuperAdmin);
            
            if (!existeSuperAdmin) {
                logger.info("SuperAdmin no existe. Procediendo a crear...");
                crearSuperAdminPorDefecto(superAdminEmail);
            } else {
                logger.info("SuperAdmin ya existe en el sistema. No es necesario crear uno nuevo.");
                
                // Mostrar información del SuperAdmin existente
                Optional<Usuario> superAdminExistente = usuarioRepository.findByEmail(superAdminEmail);
                if (superAdminExistente.isPresent()) {
                    Usuario sa = superAdminExistente.get();
                    logger.info("   ID del SuperAdmin existente: {}", sa.getId());
                    logger.info("   Nombre: {} {}", sa.getNombre(), sa.getApellido());
                    logger.info("   Rol: {}", sa.getRol());
                    logger.info("   Activo: {}", sa.isActivo());
                }
            }
            
            // Verificar si el Admin ya existe
            String adminEmail = "admin@sistema.com";
            long countAdmin = usuarioRepository.countByEmail(adminEmail);
            boolean existeAdmin = countAdmin > 0;
            logger.info("Verificando existencia del Admin:");
            logger.info("   Email a buscar: {}", adminEmail);
            logger.info("   Cantidad encontrada: {}", countAdmin);
            logger.info("   ¿Ya existe?: {}", existeAdmin);
            if (!existeAdmin) {
                logger.info("Admin no existe. Procediendo a crear...");
                crearAdminPorDefecto(adminEmail);
            } else {
                logger.info("Admin ya existe en el sistema. No es necesario crear uno nuevo.");
            }
            
            // Mostrar el total final de usuarios
            long totalFinal = usuarioRepository.count();
            logger.info("Total final de usuarios en la base de datos: {}", totalFinal);
            
            logger.info("Configuración inicial completada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error durante la inicialización de datos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar datos del sistema", e);
        }
    }
    
    private void crearSuperAdminPorDefecto(String email) {
        try {
            logger.info("Creando SuperAdmin por defecto...");
            
            SuperAdmin superAdmin = new SuperAdmin("Super", "Admin", email);
            superAdmin.setPassword(passwordEncoder.encode("secret"));
            superAdmin.setActivo(true);
            superAdmin.setBloqueado(false);
            superAdmin.setCambiarPass(true);
            
            logger.info("Datos del SuperAdmin a crear:");
            logger.info("   Email: {}", superAdmin.getEmail());
            logger.info("   Nombre: {} {}", superAdmin.getNombre(), superAdmin.getApellido());
            logger.info("   Rol: {}", superAdmin.getRol());
            logger.info("   Tipo Usuario: {}", superAdmin.getTipoUsuario());
            logger.info("   Activo: {}", superAdmin.isActivo());
            logger.info("   Bloqueado: {}", superAdmin.isBloqueado());
            
            SuperAdmin savedSuperAdmin = usuarioRepository.save(superAdmin);
            
            logger.info("SuperAdmin creado exitosamente:");
            logger.info("   ID generado: {}", savedSuperAdmin.getId());
            logger.info("   Password inicial: secret");
            logger.info("   IMPORTANTE: Cambiar la contraseña después del primer login");
            
            // Verificar que realmente se guardó
            long countVerificacion = usuarioRepository.countByEmail(email);
            logger.info("Verificación post-creación: count = {}", countVerificacion);
            
        } catch (Exception e) {
            logger.error("Error al crear SuperAdmin: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void crearAdminPorDefecto(String email) {
        try {
            logger.info("Creando Admin por defecto...");
            com.poo.miapi.model.core.Admin admin = new com.poo.miapi.model.core.Admin();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(com.poo.miapi.model.enums.Rol.ADMIN);
            admin.setActivo(true);
            admin.setBloqueado(false);
            admin.setCambiarPass(true);
            logger.info("Datos del Admin a crear:");
            logger.info("   Email: {}", admin.getEmail());
            logger.info("   Nombre: {} {}", admin.getNombre(), admin.getApellido());
            logger.info("   Rol: {}", admin.getRol());
            logger.info("   Tipo Usuario: {}", admin.getTipoUsuario());
            logger.info("   Activo: {}", admin.isActivo());
            logger.info("   Bloqueado: {}", admin.isBloqueado());
            com.poo.miapi.model.core.Admin savedAdmin = usuarioRepository.save(admin);
            logger.info("Admin creado exitosamente:");
            logger.info("   ID generado: {}", savedAdmin.getId());
            logger.info("   Password inicial: admin123");
            logger.info("   IMPORTANTE: Cambiar la contraseña después del primer login");
            long countVerificacion = usuarioRepository.countByEmail(email);
            logger.info("Verificación post-creación: count = {}", countVerificacion);
        } catch (Exception e) {
            logger.error("Error al crear Admin: {}", e.getMessage(), e);
            throw e;
        }
    }
}
