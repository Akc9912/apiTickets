-- =========================================
-- SCRIPT COMPLETO DE BASE DE DATOS
-- SISTEMA DE TICKETS v1.0.0
-- =========================================
-- 
-- Este script único crea TODO lo necesario:
-- ✅ Base de datos
-- ✅ Estructura completa
-- ✅ Datos iniciales
-- ✅ Compatible con enum Rol
-- ✅ Listo para producción
--
-- CREDENCIALES SuperAdmin (creadas por la aplicación):
-- Email: superadmin@sistema.com
-- Password: secret
-- ⚠️ CAMBIAR después del primer login
-- =========================================

-- Crear y usar la base de datos
CREATE DATABASE IF NOT EXISTS apiticket 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE apiticket;

-- Desactivar foreign key checks durante setup
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- LIMPIAR SI EXISTE (para re-ejecución)
-- =========================================

DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS incidente_tecnico;
DROP TABLE IF EXISTS historial_validacion_trabajador;
DROP TABLE IF EXISTS tecnico_por_ticket;
DROP TABLE IF EXISTS notificacion;
DROP TABLE IF EXISTS tipo_notificacion;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS super_admin;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS tecnico;
DROP TABLE IF EXISTS trabajador;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS resumen_ticket_mensual;

-- =========================================
-- TABLA PRINCIPAL DE USUARIOS
-- =========================================

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_usuario VARCHAR(31) NOT NULL, -- Discriminador JPA
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    cambiar_pass BOOLEAN NOT NULL DEFAULT TRUE,
    rol ENUM('SUPERADMIN', 'ADMIN', 'TECNICO', 'TRABAJADOR') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================
-- TABLAS DE ESPECIALIZACIÓN
-- =========================================

CREATE TABLE super_admin (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE admin (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE trabajador (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE tecnico (
    id INT PRIMARY KEY,
    fallas INT NOT NULL DEFAULT 0,
    marcas INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- =========================================
-- TABLA DE TICKETS
-- =========================================

CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    estado ENUM('No atendido', 'Atendido', 'Finalizado', 'Resuelto', 'Reabierto') NOT NULL DEFAULT 'No atendido',
    id_creador INT,
    id_tecnico_asignado INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fue_resuelto BOOLEAN DEFAULT NULL,
    motivo_falla TEXT,
    FOREIGN KEY (id_creador) REFERENCES trabajador(id) ON DELETE SET NULL,
    FOREIGN KEY (id_tecnico_asignado) REFERENCES tecnico(id) ON DELETE SET NULL
);

-- =========================================
-- HISTORIAL Y AUDITORÍA
-- =========================================

CREATE TABLE tecnico_por_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_tecnico INT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_desasignacion TIMESTAMP NULL,
    estado_inicial ENUM('No atendido', 'Atendido', 'Finalizado', 'Resuelto', 'Reabierto') NOT NULL,
    estado_final ENUM('No atendido', 'Atendido', 'Finalizado', 'Resuelto', 'Reabierto'),
    comentario TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE
);

CREATE TABLE auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    detalle TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incidente_tecnico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tecnico INT NOT NULL,
    id_ticket INT NOT NULL,
    tipo ENUM('MARCA', 'FALLA') NOT NULL,
    motivo TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE
);

CREATE TABLE historial_validacion_trabajador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajador INT NOT NULL,
    id_ticket INT NOT NULL,
    id_admin INT NOT NULL,
    fue_aprobado BOOLEAN NOT NULL,
    comentario_admin TEXT,
    fecha_validacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_trabajador) REFERENCES trabajador(id) ON DELETE CASCADE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_admin) REFERENCES admin(id) ON DELETE CASCADE
);

-- =========================================
-- SISTEMA DE NOTIFICACIONES
-- =========================================

CREATE TABLE tipo_notificacion (
    id VARCHAR(30) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE notificacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo VARCHAR(30),
    mensaje TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo) REFERENCES tipo_notificacion(id) ON DELETE SET NULL
);

-- =========================================
-- TABLA DE ESTADÍSTICAS
-- =========================================

CREATE TABLE resumen_ticket_mensual (
    anio INT NOT NULL,
    mes INT NOT NULL,
    total_creados INT NOT NULL DEFAULT 0,
    total_resueltos INT NOT NULL DEFAULT 0,
    total_reabiertos INT NOT NULL DEFAULT 0,
    promedio_tiempo_resolucion DECIMAL(10,2),
    PRIMARY KEY (anio, mes)
);

-- =========================================
-- ÍNDICES OPTIMIZADOS
-- =========================================

-- Usuario
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_rol ON usuario(rol);
CREATE INDEX idx_usuario_activo ON usuario(activo);
CREATE INDEX idx_usuario_tipo ON usuario(tipo_usuario);

-- Ticket
CREATE INDEX idx_ticket_estado ON ticket(estado);
CREATE INDEX idx_ticket_creador ON ticket(id_creador);
CREATE INDEX idx_ticket_tecnico ON ticket(id_tecnico_asignado);
CREATE INDEX idx_ticket_fecha_creacion ON ticket(fecha_creacion);

-- Relaciones
CREATE INDEX idx_tecnico_ticket ON tecnico_por_ticket(id_ticket, id_tecnico);
CREATE INDEX idx_notificacion_usuario ON notificacion(id_usuario);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);

-- Reactivar foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- DATOS INICIALES
-- =========================================

-- Tipos de notificación
INSERT INTO tipo_notificacion (id, descripcion) VALUES
('TICKET_ASIGNADO', 'Ticket asignado a técnico'),
('TICKET_RESUELTO', 'Ticket marcado como resuelto'),
('TICKET_REABIERTO', 'Ticket reabierto'),
('TICKET_FINALIZADO', 'Ticket finalizado'),
('USUARIO_BLOQUEADO', 'Usuario bloqueado por el sistema'),
('USUARIO_DESBLOQUEADO', 'Usuario desbloqueado'),
('PASSWORD_RESET', 'Contraseña reseteada por administrador'),
('ROL_CAMBIADO', 'Rol de usuario modificado'),
('CUENTA_ACTIVADA', 'Cuenta de usuario activada'),
('CUENTA_DESACTIVADA', 'Cuenta de usuario desactivada');

-- =========================================
-- VERIFICACIÓN FINAL
-- =========================================

SELECT 
    '✅ Base de datos inicializada correctamente' as status,
    (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'apiticket') as tablas_creadas,
    (SELECT COUNT(*) FROM tipo_notificacion) as tipos_notificacion,
    DATABASE() as base_datos_actual,
    CURRENT_TIMESTAMP as fecha_creacion;

-- =========================================
-- INSTRUCCIONES FINALES
-- =========================================
/*
🎯 SIGUIENTE PASO:
   Iniciar la aplicación Spring Boot:
   ./mvnw spring-boot:run

🔐 CREDENCIALES SuperAdmin (creadas automáticamente):
   Email: superadmin@sistema.com
   Password: secret
   
⚠️  CAMBIAR LA CONTRASEÑA INMEDIATAMENTE

✅ COMPLETADO:
   - Base de datos 'apiticket' creada
   - Estructura completa con enum Rol
   - Índices optimizados
   - Datos básicos insertados
   - Listo para Spring Boot
*/
