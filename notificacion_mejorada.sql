-- =========================================
-- TABLA DE NOTIFICACIONES MEJORADA
-- SISTEMA DE TICKETS v1.0.0
-- =========================================
--
-- Mejoras implementadas:
-- ✅ Estados de notificación (leída/no leída)
-- ✅ Tipos y categorías de notificación
-- ✅ Prioridades y severidades
-- ✅ Referencias a entidades relacionadas
-- ✅ Metadatos adicionales
-- ✅ Fechas de expiración y lectura
-- ✅ Acciones automáticas
-- ✅ Soft delete para auditoría
-- ✅ Índices optimizados
-- =========================================

USE apiticket;

-- Eliminar tabla existente si existe
DROP TABLE IF EXISTS notificacion;

-- =========================================
-- TABLA DE NOTIFICACIONES COMPLETA
-- =========================================

CREATE TABLE notificacion (
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Información del destinatario
    usuario_id INT NOT NULL,

    -- Contenido de la notificación
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,

    -- Tipo y categoría de notificación
    tipo ENUM(
        'TICKET_ASIGNADO',
        'TICKET_RESUELTO',
        'TICKET_REABIERTO',
        'TICKET_FINALIZADO',
        'MARCA_ASIGNADA',
        'FALLA_REGISTRADA',
        'USUARIO_BLOQUEADO',
        'PASSWORD_RESET',
        'VALIDACION_REQUERIDA',
        'SISTEMA_MANTENIMIENTO',
        'RECORDATORIO',
        'ALERTA_SEGURIDAD',
        'INFO_GENERAL'
    ) NOT NULL DEFAULT 'INFO_GENERAL',

    categoria ENUM(
        'TICKETS',
        'USUARIOS',
        'SEGURIDAD',
        'SISTEMA',
        'RECORDATORIOS'
    ) NOT NULL DEFAULT 'SISTEMA',

    -- Prioridad y severidad
    prioridad ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL DEFAULT 'MEDIA',
    severidad ENUM('INFO', 'WARNING', 'ERROR', 'CRITICAL') NOT NULL DEFAULT 'INFO',

    -- Estado de la notificación
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    archivada BOOLEAN NOT NULL DEFAULT FALSE,
    eliminada BOOLEAN NOT NULL DEFAULT FALSE,

    -- Referencias a entidades relacionadas
    entidad_tipo VARCHAR(50) NULL, -- 'TICKET', 'USUARIO', 'TECNICO', etc.
    entidad_id INT NULL, -- ID de la entidad relacionada

    -- Información adicional
    datos_adicionales JSON NULL, -- Metadatos adicionales en formato JSON
    accion_automatica VARCHAR(100) NULL, -- Acción que se puede ejecutar automáticamente
    url_accion VARCHAR(500) NULL, -- URL para redirigir al hacer clic

    -- Configuración de visualización
    icono VARCHAR(50) DEFAULT 'info',
    color VARCHAR(20) DEFAULT 'blue',
    mostrar_badge BOOLEAN NOT NULL DEFAULT TRUE,

    -- Control de tiempo
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_lectura TIMESTAMP NULL,
    fecha_archivado TIMESTAMP NULL,
    fecha_expiracion TIMESTAMP NULL,

    -- Auditoría
    creado_por INT NULL, -- Usuario que generó la notificación (puede ser automático)
    ip_origen VARCHAR(45) NULL,
    user_agent TEXT NULL,

    -- Soft delete
    fecha_eliminacion TIMESTAMP NULL,
    eliminado_por INT NULL,

    -- Claves foráneas
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (creado_por) REFERENCES usuario(id) ON DELETE SET NULL,
    FOREIGN KEY (eliminado_por) REFERENCES usuario(id) ON DELETE SET NULL
);

-- =========================================
-- ÍNDICES OPTIMIZADOS
-- =========================================

-- Índices principales para consultas frecuentes
CREATE INDEX idx_notificacion_usuario_activa ON notificacion(usuario_id, eliminada, fecha_creacion DESC);
CREATE INDEX idx_notificacion_no_leidas ON notificacion(usuario_id, leida, eliminada);
CREATE INDEX idx_notificacion_tipo ON notificacion(tipo, categoria);
CREATE INDEX idx_notificacion_prioridad ON notificacion(prioridad, severidad);
CREATE INDEX idx_notificacion_entidad ON notificacion(entidad_tipo, entidad_id);
CREATE INDEX idx_notificacion_fecha_creacion ON notificacion(fecha_creacion DESC);
CREATE INDEX idx_notificacion_fecha_expiracion ON notificacion(fecha_expiracion);

-- Índice compuesto para dashboard
CREATE INDEX idx_notificacion_dashboard ON notificacion(usuario_id, leida, archivada, eliminada, fecha_creacion DESC);

-- =========================================
-- VISTAS ÚTILES
-- =========================================

-- Vista para notificaciones activas por usuario
CREATE OR REPLACE VIEW v_notificaciones_activas AS
SELECT
    n.id,
    n.usuario_id,
    u.nombre as usuario_nombre,
    u.email as usuario_email,
    n.titulo,
    n.mensaje,
    n.tipo,
    n.categoria,
    n.prioridad,
    n.severidad,
    n.leida,
    n.archivada,
    n.entidad_tipo,
    n.entidad_id,
    n.icono,
    n.color,
    n.fecha_creacion,
    n.fecha_lectura,
    CASE
        WHEN n.fecha_expiracion IS NULL THEN FALSE
        WHEN n.fecha_expiracion < NOW() THEN TRUE
        ELSE FALSE
    END as expirada
FROM notificacion n
JOIN usuario u ON n.usuario_id = u.id
WHERE n.eliminada = FALSE
ORDER BY n.fecha_creacion DESC;

-- Vista para estadísticas de notificaciones
CREATE OR REPLACE VIEW v_estadisticas_notificaciones AS
SELECT
    u.id as usuario_id,
    u.nombre,
    u.email,
    COUNT(*) as total_notificaciones,
    COUNT(CASE WHEN n.leida = FALSE THEN 1 END) as no_leidas,
    COUNT(CASE WHEN n.archivada = TRUE THEN 1 END) as archivadas,
    COUNT(CASE WHEN n.prioridad = 'CRITICA' THEN 1 END) as criticas,
    MAX(n.fecha_creacion) as ultima_notificacion
FROM usuario u
LEFT JOIN notificacion n ON u.id = n.usuario_id AND n.eliminada = FALSE
GROUP BY u.id, u.nombre, u.email;

-- =========================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- =========================================

DELIMITER //

-- Procedimiento para crear notificación
CREATE PROCEDURE sp_crear_notificacion(
    IN p_usuario_id INT,
    IN p_titulo VARCHAR(255),
    IN p_mensaje TEXT,
    IN p_tipo VARCHAR(50),
    IN p_categoria VARCHAR(50),
    IN p_prioridad VARCHAR(20),
    IN p_entidad_tipo VARCHAR(50),
    IN p_entidad_id INT,
    IN p_creado_por INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO notificacion (
        usuario_id, titulo, mensaje, tipo, categoria, prioridad,
        entidad_tipo, entidad_id, creado_por
    ) VALUES (
        p_usuario_id, p_titulo, p_mensaje, p_tipo, p_categoria, p_prioridad,
        p_entidad_tipo, p_entidad_id, p_creado_por
    );

    COMMIT;
    SELECT LAST_INSERT_ID() as notificacion_id;
END //

-- Procedimiento para marcar como leída
CREATE PROCEDURE sp_marcar_leida(
    IN p_notificacion_id INT,
    IN p_usuario_id INT
)
BEGIN
    UPDATE notificacion
    SET leida = TRUE, fecha_lectura = CURRENT_TIMESTAMP
    WHERE id = p_notificacion_id
      AND usuario_id = p_usuario_id
      AND eliminada = FALSE;

    SELECT ROW_COUNT() as filas_afectadas;
END //

-- Procedimiento para limpiar notificaciones expiradas
CREATE PROCEDURE sp_limpiar_notificaciones_expiradas()
BEGIN
    UPDATE notificacion
    SET eliminada = TRUE, fecha_eliminacion = CURRENT_TIMESTAMP
    WHERE fecha_expiracion IS NOT NULL
      AND fecha_expiracion < CURRENT_TIMESTAMP
      AND eliminada = FALSE;

    SELECT ROW_COUNT() as notificaciones_limpiadas;
END //

DELIMITER ;

-- =========================================
-- TRIGGERS PARA AUDITORÍA
-- =========================================

DELIMITER //

-- Trigger para registrar creación de notificaciones
CREATE TRIGGER tr_notificacion_created
AFTER INSERT ON notificacion
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (
        usuario, accion, entidad, detalle, fecha
    ) VALUES (
        COALESCE((SELECT email FROM usuario WHERE id = NEW.creado_por), 'SISTEMA'),
        'CREATE_NOTIFICATION',
        'NOTIFICACION',
        CONCAT('Notificación creada para usuario ID: ', NEW.usuario_id, ' - Tipo: ', NEW.tipo),
        NOW()
    );
END //

DELIMITER ;

-- =========================================
-- VERIFICACIÓN FINAL
-- =========================================

SELECT
    'Tabla de notificaciones mejorada creada correctamente' as status,
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = 'apiticket' AND TABLE_NAME = 'notificacion') as columnas_creadas,
    (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = 'apiticket' AND TABLE_NAME = 'notificacion') as indices_creados,
    CURRENT_TIMESTAMP as fecha_creacion;

-- Mostrar estructura de la tabla
DESCRIBE notificacion;
