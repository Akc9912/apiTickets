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
DROP TABLE IF EXISTS historial_validacion;
DROP TABLE IF EXISTS tecnico_por_ticket;
DROP TABLE IF EXISTS notificacion;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS super_admin;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS tecnico;
DROP TABLE IF EXISTS trabajador;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS resumen_ticket_mensual;
DROP TABLE IF EXISTS solicitud_devolucion;
DROP TABLE IF EXISTS estadistica_tecnico;
DROP TABLE IF EXISTS estadistica_periodo;

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
    rol ENUM('SUPER_ADMIN', 'ADMIN', 'TECNICO', 'TRABAJADOR') NOT NULL,
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
    estado ENUM('NO_ATENDIDO', 'ATENDIDO', 'RESUELTO', 'FINALIZADO', 'REABIERTO') NOT NULL DEFAULT 'NO_ATENDIDO',
    id_creador INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_creador) REFERENCES usuario(id) ON DELETE SET NULL
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
    estado_inicial ENUM('NO_ATENDIDO', 'ATENDIDO', 'RESUELTO', 'FINALIZADO', 'REABIERTO') NOT NULL,
    estado_final ENUM('NO_ATENDIDO', 'ATENDIDO', 'RESUELTO', 'FINALIZADO', 'REABIERTO'),
    comentario TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE
);

CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL,
    nombre_usuario VARCHAR(100) NOT NULL,
    email_usuario VARCHAR(100) NULL,
    rol_usuario ENUM('SUPER_ADMIN', 'ADMIN', 'TECNICO', 'TRABAJADOR') NULL,
    accion ENUM('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'LOGIN_FAILED',
                'ASSIGN_TICKET', 'UNASSIGN_TICKET', 'RESOLVE_TICKET', 'REOPEN_TICKET',
                'APPROVE_RETURN', 'REJECT_RETURN', 'REQUEST_RETURN',
                'BLOCK_USER', 'UNBLOCK_USER', 'ACTIVATE_USER', 'DEACTIVATE_USER',
                'CHANGE_PASSWORD', 'RESET_PASSWORD', 'EVALUATE_TICKET', 'CREATE_INCIDENT') NOT NULL,
    entidad_tipo VARCHAR(50) NOT NULL,
    entidad_id INT NULL,
    entidad_nombre VARCHAR(255) NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    session_id VARCHAR(128) NULL,
    valores_anteriores JSON NULL,
    valores_nuevos JSON NULL,
    descripcion TEXT NULL,
    resultado ENUM('SUCCESS', 'ERROR', 'WARNING') NOT NULL DEFAULT 'SUCCESS',
    mensaje_error TEXT NULL,
    origen ENUM('WEB', 'API', 'SYSTEM', 'MIGRATION') NOT NULL DEFAULT 'WEB',
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    procesado BOOLEAN NOT NULL DEFAULT FALSE,

    INDEX idx_auditoria_usuario (id_usuario),
    INDEX idx_auditoria_accion (accion),
    INDEX idx_auditoria_fecha (fecha),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

CREATE TABLE historial_validacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_validador INT NOT NULL,
    id_ticket INT NOT NULL,
    fue_aprobado BOOLEAN NOT NULL,
    comentario TEXT,
    fecha_validacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario_validador) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE
);

-- =========================================
-- SISTEMA DE NOTIFICACIONES
-- =========================================

CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    origen_tipo VARCHAR(50) NULL,   -- 'TICKET', 'SISTEMA', etc.
    origen_id BIGINT NULL,          -- ID del ticket, usuario u otro recurso
    metadata JSON NULL,             -- Datos adicionales, URLs, acciones
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notificacion_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notificacion_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_lectura DATETIME NULL,
    FOREIGN KEY (notificacion_id) REFERENCES notificacion(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
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
CREATE INDEX idx_ticket_fecha_creacion ON ticket(fecha_creacion);

-- Relaciones y auditoría
CREATE INDEX idx_tecnico_ticket ON tecnico_por_ticket(id_ticket, id_tecnico);
CREATE INDEX idx_tecnico_ticket_fecha ON tecnico_por_ticket(fecha_asignacion);
CREATE INDEX idx_tecnico_ticket_activo ON tecnico_por_ticket(activo);

-- Incidentes técnicos
CREATE INDEX idx_incidente_tecnico ON incidente_tecnico(id_tecnico);
CREATE INDEX idx_incidente_ticket ON incidente_tecnico(id_ticket);
CREATE INDEX idx_incidente_tipo ON incidente_tecnico(tipo);
CREATE INDEX idx_incidente_fecha ON incidente_tecnico(fecha_registro);

-- Historial validación
CREATE INDEX idx_validacion_ticket ON historial_validacion(id_ticket);
CREATE INDEX idx_validacion_usuario ON historial_validacion(id_usuario_validador);
CREATE INDEX idx_validacion_fecha ON historial_validacion(fecha_validacion);
CREATE INDEX idx_validacion_aprobado ON historial_validacion(fue_aprobado);

-- =========================================
-- ESTADÍSTICAS POR PERÍODO
-- =========================================

CREATE TABLE estadistica_periodo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,
    tickets_creados INT NOT NULL DEFAULT 0,
    tickets_asignados INT NOT NULL DEFAULT 0,
    tickets_resueltos INT NOT NULL DEFAULT 0,
    tickets_finalizados INT NOT NULL DEFAULT 0,
    tickets_reabiertos INT NOT NULL DEFAULT 0,
    tickets_pendientes INT NOT NULL DEFAULT 0,
    tiempo_promedio_asignacion DECIMAL(10,2) DEFAULT 0,
    tiempo_promedio_resolucion DECIMAL(10,2) DEFAULT 0,
    tiempo_maximo_resolucion DECIMAL(10,2) DEFAULT 0,
    tiempo_minimo_resolucion DECIMAL(10,2) DEFAULT 0,
    tickets_aprobados INT NOT NULL DEFAULT 0,
    tickets_rechazados INT NOT NULL DEFAULT 0,
    porcentaje_aprobacion DECIMAL(5,2) DEFAULT 0,
    solicitudes_devolucion INT NOT NULL DEFAULT 0,
    devoluciones_aprobadas INT NOT NULL DEFAULT 0,
    devoluciones_rechazadas INT NOT NULL DEFAULT 0,
    calculado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos_completos BOOLEAN NOT NULL DEFAULT TRUE,
    notas TEXT NULL,

    UNIQUE KEY unique_periodo (periodo_tipo, anio, mes, semana, dia, trimestre),
    INDEX idx_periodo_fecha (anio, mes, dia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- ESTADÍSTICAS POR TÉCNICO
-- =========================================

CREATE TABLE estadistica_tecnico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tecnico INT NOT NULL,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,
    tickets_asignados INT NOT NULL DEFAULT 0,
    tickets_completados INT NOT NULL DEFAULT 0,
    tickets_aprobados INT NOT NULL DEFAULT 0,
    tickets_rechazados INT NOT NULL DEFAULT 0,
    tiempo_promedio_resolucion DECIMAL(10,2) DEFAULT 0,
    tickets_dentro_sla INT NOT NULL DEFAULT 0,
    porcentaje_aprobacion DECIMAL(5,2) DEFAULT 0,
    marcas_recibidas INT NOT NULL DEFAULT 0,
    fallas_registradas INT NOT NULL DEFAULT 0,
    solicitudes_devolucion INT NOT NULL DEFAULT 0,
    devoluciones_aprobadas INT NOT NULL DEFAULT 0,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_estadistica_tecnico (id_tecnico, periodo_tipo),
    UNIQUE KEY uk_tecnico_periodo (id_tecnico, periodo_tipo, anio, mes, semana, dia, trimestre),
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- SOLICITUDES DE DEVOLUCIÓN
-- =========================================

CREATE TABLE solicitud_devolucion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tecnico INT NOT NULL,
    id_ticket INT NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,
    id_admin_resolutor INT NULL,
    comentario_resolucion VARCHAR(200) NULL,

    INDEX idx_solicitud_tecnico (id_tecnico),
    INDEX idx_solicitud_ticket (id_ticket),
    INDEX idx_solicitud_estado (estado),
    INDEX idx_solicitud_fecha (fecha_solicitud),
    INDEX idx_solicitud_admin (id_admin_resolutor),
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_admin_resolutor) REFERENCES admin(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- =========================================

-- Estadísticas período - índices adicionales
CREATE INDEX idx_periodo_tipo ON estadistica_periodo(periodo_tipo);
CREATE INDEX idx_periodo_calculado ON estadistica_periodo(calculado_en);
CREATE INDEX idx_periodo_completos ON estadistica_periodo(datos_completos);

-- Estadísticas técnico - índices adicionales
CREATE INDEX idx_estadistica_fecha ON estadistica_tecnico(anio, mes);
CREATE INDEX idx_estadistica_calculo ON estadistica_tecnico(fecha_calculo);

-- Notificaciones - índices adicionales
CREATE INDEX idx_notif_tipo ON notificacion(tipo);
CREATE INDEX idx_notif_categoria ON notificacion(categoria);
CREATE INDEX idx_notif_prioridad ON notificacion(prioridad);
CREATE INDEX idx_notif_ticket ON notificacion(ticket_id);
CREATE INDEX idx_notif_eliminada ON notificacion(eliminada);

-- Auditoría - índices adicionales
CREATE INDEX idx_auditoria_entidad ON auditoria(entidad_tipo, entidad_id);
CREATE INDEX idx_auditoria_resultado ON auditoria(resultado);
CREATE INDEX idx_auditoria_origen ON auditoria(origen);
CREATE INDEX idx_auditoria_procesado ON auditoria(procesado);

-- Reactivar foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- DATOS INICIALES
-- =========================================
INSERT INTO auditoria (nombre_usuario, accion, entidad_tipo, descripcion, origen) VALUES
('SYSTEM', 'CREATE', 'DATABASE', 'Estructura de base de datos creada', 'SYSTEM');
-- =========================================
-- VERIFICACIÓN FINAL
-- =========================================

SELECT
    'Base de datos inicializada correctamente' as status,
    (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'apiticket') as tablas_creadas,
    DATABASE() as base_datos_actual,
    CURRENT_TIMESTAMP as fecha_creacion;


