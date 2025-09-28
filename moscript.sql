-- =========================================
-- MEJORAS PARA LA TABLA DE AUDITORÍA
-- Sistema de Tickets - Versión Mejorada
-- =========================================

USE apiticket;

-- Eliminar la tabla de auditoría actual
DROP TABLE IF EXISTS auditoria;

-- Crear nueva tabla de auditoría mejorada
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- Información del usuario que realizó la acción
    id_usuario INT NULL,
    nombre_usuario VARCHAR(100) NOT NULL,
    email_usuario VARCHAR(100) NULL,
    rol_usuario ENUM('SUPER_ADMIN', 'ADMIN', 'TECNICO', 'TRABAJADOR') NULL,

    -- Detalles de la acción
    accion ENUM(
        'CREATE', 'UPDATE', 'DELETE',
        'LOGIN', 'LOGOUT', 'LOGIN_FAILED',
        'ASSIGN_TICKET', 'UNASSIGN_TICKET', 'RESOLVE_TICKET', 'REOPEN_TICKET',
        'APPROVE_RETURN', 'REJECT_RETURN', 'REQUEST_RETURN',
        'BLOCK_USER', 'UNBLOCK_USER', 'ACTIVATE_USER', 'DEACTIVATE_USER',
        'CHANGE_PASSWORD', 'RESET_PASSWORD',
        'EVALUATE_TICKET', 'CREATE_INCIDENT'
    ) NOT NULL,

    -- Entidad afectada
    entidad_tipo VARCHAR(50) NOT NULL, -- 'TICKET', 'USUARIO', 'TECNICO', etc.
    entidad_id INT NULL, -- ID del registro afectado

    -- Contexto adicional
    detalle_accion TEXT NULL, -- Descripción detallada de lo que se hizo
    valores_anteriores JSON NULL, -- Estado anterior (para UPDATE/DELETE)
    valores_nuevos JSON NULL, -- Estado nuevo (para CREATE/UPDATE)

    -- Metadata técnica
    direccion_ip VARCHAR(45) NULL, -- IPv4 o IPv6
    user_agent TEXT NULL, -- Información del navegador/cliente
    sesion_id VARCHAR(255) NULL, -- ID de sesión si aplica

    -- Timestamps
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_procesamiento TIMESTAMP NULL, -- Para auditorías asíncronas

    -- Clasificación y severidad
    categoria ENUM('SECURITY', 'DATA', 'SYSTEM', 'BUSINESS') DEFAULT 'BUSINESS',
    severidad ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',

    -- Para integridad y compliance
    hash_integridad VARCHAR(64) NULL, -- Hash para verificar integridad del registro
    retencion_hasta DATE NULL, -- Fecha hasta cuando retener el registro

    -- Índices para mejorar búsquedas
    INDEX idx_auditoria_usuario (id_usuario),
    INDEX idx_auditoria_fecha (fecha_accion),
    INDEX idx_auditoria_accion (accion),
    INDEX idx_auditoria_entidad (entidad_tipo, entidad_id),
    INDEX idx_auditoria_categoria (categoria, severidad),
    INDEX idx_auditoria_ip (direccion_ip),
    INDEX idx_auditoria_compuesto (entidad_tipo, accion, fecha_accion),

    -- Relación con usuario si existe
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

-- =========================================
-- TABLA DE CONFIGURACIÓN DE AUDITORÍA
-- =========================================

CREATE TABLE auditoria_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entidad_tipo VARCHAR(50) NOT NULL,
    accion ENUM(
        'CREATE', 'UPDATE', 'DELETE',
        'LOGIN', 'LOGOUT', 'LOGIN_FAILED',
        'ASSIGN_TICKET', 'UNASSIGN_TICKET', 'RESOLVE_TICKET', 'REOPEN_TICKET',
        'APPROVE_RETURN', 'REJECT_RETURN', 'REQUEST_RETURN',
        'BLOCK_USER', 'UNBLOCK_USER', 'ACTIVATE_USER', 'DEACTIVATE_USER',
        'CHANGE_PASSWORD', 'RESET_PASSWORD',
        'EVALUATE_TICKET', 'CREATE_INCIDENT'
    ) NOT NULL,
    auditoria_habilitada BOOLEAN DEFAULT TRUE,
    incluir_valores_anteriores BOOLEAN DEFAULT FALSE,
    incluir_valores_nuevos BOOLEAN DEFAULT TRUE,
    categoria ENUM('SECURITY', 'DATA', 'SYSTEM', 'BUSINESS') DEFAULT 'BUSINESS',
    severidad ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    dias_retencion INT DEFAULT 2555, -- 7 años por defecto
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY unique_entidad_accion (entidad_tipo, accion)
);

-- =========================================
-- VISTA PARA CONSULTAS SIMPLIFICADAS
-- =========================================

CREATE VIEW v_auditoria_resumen AS
SELECT
    a.id,
    a.nombre_usuario,
    a.rol_usuario,
    a.accion,
    a.entidad_tipo,
    a.entidad_id,
    a.detalle_accion,
    a.categoria,
    a.severidad,
    a.direccion_ip,
    a.fecha_accion,
    CASE
        WHEN a.fecha_accion >= DATE_SUB(NOW(), INTERVAL 1 DAY) THEN 'HOY'
        WHEN a.fecha_accion >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 'ESTA_SEMANA'
        WHEN a.fecha_accion >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 'ESTE_MES'
        ELSE 'ANTERIOR'
    END as periodo
FROM auditoria a
ORDER BY a.fecha_accion DESC;

-- =========================================
-- PROCEDIMIENTO PARA INSERTAR AUDITORÍAS
-- =========================================

DELIMITER //

CREATE PROCEDURE sp_insertar_auditoria(
    IN p_id_usuario INT,
    IN p_nombre_usuario VARCHAR(100),
    IN p_email_usuario VARCHAR(100),
    IN p_rol_usuario VARCHAR(20),
    IN p_accion VARCHAR(50),
    IN p_entidad_tipo VARCHAR(50),
    IN p_entidad_id INT,
    IN p_detalle_accion TEXT,
    IN p_valores_anteriores JSON,
    IN p_valores_nuevos JSON,
    IN p_direccion_ip VARCHAR(45),
    IN p_user_agent TEXT,
    IN p_sesion_id VARCHAR(255),
    IN p_categoria VARCHAR(20),
    IN p_severidad VARCHAR(20)
)
BEGIN
    DECLARE v_dias_retencion INT DEFAULT 2555;
    DECLARE v_hash_integridad VARCHAR(64);

    -- Obtener configuración de retención
    SELECT dias_retencion INTO v_dias_retencion
    FROM auditoria_config
    WHERE entidad_tipo = p_entidad_tipo AND accion = p_accion
    LIMIT 1;

    -- Calcular hash de integridad
    SET v_hash_integridad = SHA2(CONCAT(
        IFNULL(p_id_usuario, ''),
        p_nombre_usuario,
        p_accion,
        p_entidad_tipo,
        IFNULL(p_entidad_id, ''),
        NOW()
    ), 256);

    -- Insertar registro de auditoría
    INSERT INTO auditoria (
        id_usuario, nombre_usuario, email_usuario, rol_usuario,
        accion, entidad_tipo, entidad_id, detalle_accion,
        valores_anteriores, valores_nuevos,
        direccion_ip, user_agent, sesion_id,
        categoria, severidad, hash_integridad, retencion_hasta
    ) VALUES (
        p_id_usuario, p_nombre_usuario, p_email_usuario, p_rol_usuario,
        p_accion, p_entidad_tipo, p_entidad_id, p_detalle_accion,
        p_valores_anteriores, p_valores_nuevos,
        p_direccion_ip, p_user_agent, p_sesion_id,
        IFNULL(p_categoria, 'BUSINESS'), IFNULL(p_severidad, 'MEDIUM'),
        v_hash_integridad, DATE_ADD(CURDATE(), INTERVAL v_dias_retencion DAY)
    );
END //

DELIMITER ;

-- =========================================
-- CONFIGURACIÓN INICIAL DE AUDITORÍA
-- =========================================

INSERT INTO auditoria_config (entidad_tipo, accion, auditoria_habilitada, incluir_valores_anteriores, incluir_valores_nuevos, categoria, severidad, dias_retencion) VALUES
-- Usuarios
('USUARIO', 'CREATE', TRUE, FALSE, TRUE, 'SECURITY', 'MEDIUM', 2555),
('USUARIO', 'UPDATE', TRUE, TRUE, TRUE, 'SECURITY', 'MEDIUM', 2555),
('USUARIO', 'DELETE', TRUE, TRUE, FALSE, 'SECURITY', 'HIGH', 2555),
('USUARIO', 'LOGIN', TRUE, FALSE, FALSE, 'SECURITY', 'LOW', 365),
('USUARIO', 'LOGOUT', TRUE, FALSE, FALSE, 'SECURITY', 'LOW', 365),
('USUARIO', 'LOGIN_FAILED', TRUE, FALSE, FALSE, 'SECURITY', 'HIGH', 2555),
('USUARIO', 'BLOCK_USER', TRUE, TRUE, TRUE, 'SECURITY', 'HIGH', 2555),
('USUARIO', 'UNBLOCK_USER', TRUE, TRUE, TRUE, 'SECURITY', 'HIGH', 2555),
('USUARIO', 'ACTIVATE_USER', TRUE, TRUE, TRUE, 'SECURITY', 'MEDIUM', 2555),
('USUARIO', 'DEACTIVATE_USER', TRUE, TRUE, TRUE, 'SECURITY', 'MEDIUM', 2555),
('USUARIO', 'CHANGE_PASSWORD', TRUE, FALSE, FALSE, 'SECURITY', 'MEDIUM', 2555),
('USUARIO', 'RESET_PASSWORD', TRUE, FALSE, FALSE, 'SECURITY', 'HIGH', 2555),

-- Tickets
('TICKET', 'CREATE', TRUE, FALSE, TRUE, 'BUSINESS', 'LOW', 1825),
('TICKET', 'UPDATE', TRUE, TRUE, TRUE, 'BUSINESS', 'LOW', 1825),
('TICKET', 'DELETE', TRUE, TRUE, FALSE, 'BUSINESS', 'MEDIUM', 2555),
('TICKET', 'ASSIGN_TICKET', TRUE, TRUE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('TICKET', 'UNASSIGN_TICKET', TRUE, TRUE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('TICKET', 'RESOLVE_TICKET', TRUE, TRUE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('TICKET', 'REOPEN_TICKET', TRUE, TRUE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('TICKET', 'EVALUATE_TICKET', TRUE, FALSE, TRUE, 'BUSINESS', 'MEDIUM', 1825),

-- Solicitudes de devolución
('SOLICITUD_DEVOLUCION', 'CREATE', TRUE, FALSE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('SOLICITUD_DEVOLUCION', 'APPROVE_RETURN', TRUE, TRUE, TRUE, 'BUSINESS', 'HIGH', 2555),
('SOLICITUD_DEVOLUCION', 'REJECT_RETURN', TRUE, TRUE, TRUE, 'BUSINESS', 'MEDIUM', 1825),
('SOLICITUD_DEVOLUCION', 'REQUEST_RETURN', TRUE, FALSE, TRUE, 'BUSINESS', 'MEDIUM', 1825),

-- Incidentes técnicos
('INCIDENTE_TECNICO', 'CREATE_INCIDENT', TRUE, FALSE, TRUE, 'BUSINESS', 'HIGH', 2555);

-- =========================================
-- FUNCIÓN PARA LIMPIEZA AUTOMÁTICA
-- =========================================

DELIMITER //

CREATE PROCEDURE sp_limpiar_auditoria_antigua()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_registros_eliminados INT DEFAULT 0;

    -- Eliminar registros que han superado su fecha de retención
    DELETE FROM auditoria
    WHERE retencion_hasta IS NOT NULL
    AND retencion_hasta < CURDATE();

    SET v_registros_eliminados = ROW_COUNT();

    -- Log de la limpieza
    INSERT INTO auditoria (
        nombre_usuario, accion, entidad_tipo,
        detalle_accion, categoria, severidad
    ) VALUES (
        'SISTEMA', 'DELETE', 'AUDITORIA',
        CONCAT('Limpieza automática: ', v_registros_eliminados, ' registros eliminados'),
        'SYSTEM', 'LOW'
    );

END //

DELIMITER ;

-- =========================================
-- EVENTO PARA LIMPIEZA AUTOMÁTICA SEMANAL
-- =========================================

-- Habilitar el programador de eventos
SET GLOBAL event_scheduler = ON;

-- Crear evento para limpieza semanal
DROP EVENT IF EXISTS evt_limpiar_auditoria;

CREATE EVENT evt_limpiar_auditoria
ON SCHEDULE EVERY 1 WEEK
STARTS '2025-01-01 02:00:00'
DO
    CALL sp_limpiar_auditoria_antigua();

-- =========================================
-- ÍNDICES ADICIONALES PARA PERFORMANCE
-- =========================================

-- Para búsquedas por rango de fechas y entidad
CREATE INDEX idx_auditoria_fecha_entidad ON auditoria(fecha_accion, entidad_tipo, entidad_id);

-- Para búsquedas de seguridad (índice filtrado)
CREATE INDEX idx_auditoria_security ON auditoria(categoria, severidad, fecha_accion);

-- Para reportes de actividad por usuario
CREATE INDEX idx_auditoria_usuario_fecha ON auditoria(id_usuario, fecha_accion);

-- =========================================
-- VERIFICACIÓN Y RESUMEN
-- =========================================

SELECT
    'Auditoría mejorada instalada correctamente' as status,
    (SELECT COUNT(*) FROM auditoria_config) as configuraciones_creadas,
    'sp_insertar_auditoria, sp_limpiar_auditoria_antigua' as procedimientos_creados,
    'v_auditoria_resumen' as vistas_creadas,
    'evt_limpiar_auditoria' as eventos_creados,
    CURRENT_TIMESTAMP as fecha_instalacion;
