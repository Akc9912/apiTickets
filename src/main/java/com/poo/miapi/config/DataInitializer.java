package com.poo.miapi.config;

import com.poo.miapi.model.core.SuperAdmin;
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
            logger.info("🚀 Iniciando configuración inicial de datos...");
            
            // Mostrar todos los usuarios actuales en la base de datos
            long totalUsuarios = usuarioRepository.count();
            logger.info("📊 Total de usuarios en la base de datos: {}", totalUsuarios);
            
            // SOLUCIÓN: Limpiar cualquier usuario con discriminador incorrecto usando SQL nativo
            // Esto eliminará registros con discriminador "SUPER_ADMIN" que causan conflicto
            try {
                usuarioRepository.deleteByEmail("superadmin@sistema.com");
                logger.info("🧹 Limpieza completada: eliminados registros previos con discriminador inconsistente");
            } catch (Exception e) {
                logger.warn("⚠️  No se encontraron registros previos para limpiar: {}", e.getMessage());
            }
            
            // Simplificar la verificación: usar count directo
            long countSuperAdmin = usuarioRepository.countByEmail("superadmin@sistema.com");
            boolean existeSuperAdmin = countSuperAdmin > 0;
            logger.info("🔍 ¿Existe SuperAdmin con email 'superadmin@sistema.com'? {} (count: {})", existeSuperAdmin, countSuperAdmin);
            
            if (!existeSuperAdmin) {
                logger.info("📦 Creando SuperAdmin por defecto...");
                
                SuperAdmin superAdmin = new SuperAdmin("Super", "Admin", "superadmin@sistema.com");
                superAdmin.setPassword(passwordEncoder.encode("secret"));
                superAdmin.setActivo(true);
                superAdmin.setBloqueado(false);
                superAdmin.setCambiarPass(true);
                
                SuperAdmin savedSuperAdmin = usuarioRepository.save(superAdmin);
                
                logger.info("✅ SuperAdmin creado exitosamente:");
                logger.info("   🆔 ID: {}", savedSuperAdmin.getId());
                logger.info("   📧 Email: {}", savedSuperAdmin.getEmail());
                logger.info("   👤 Nombre: {} {}", savedSuperAdmin.getNombre(), savedSuperAdmin.getApellido());
                logger.info("   🔑 Password: secret");
                logger.info("   ⚡ Rol: {}", savedSuperAdmin.getRol());
                logger.info("   📝 Tipo Usuario: {}", savedSuperAdmin.getTipoUsuario());
                logger.info("   ⚠️  IMPORTANTE: Cambiar la contraseña después del primer login");
                
                // Verificar nuevamente después de guardar
                long countFinal = usuarioRepository.countByEmail("superadmin@sistema.com");
                logger.info("🔄 Verificación post-creación: count = {}", countFinal);
            } else {
                logger.info("ℹ️  SuperAdmin ya existe en el sistema");
            }
            
            // Mostrar el total final de usuarios
            long totalFinal = usuarioRepository.count();
            logger.info("📊 Total final de usuarios en la base de datos: {}", totalFinal);
            
            logger.info("🎉 Configuración inicial completada");
            
        } catch (Exception e) {
            logger.error("❌ Error durante la inicialización de datos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar datos del sistema", e);
        }
    }
}
