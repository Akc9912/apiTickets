-- =========================================
-- ACTUALIZACIÓN SISTEMA DE ESTADÍSTICAS
-- SISTEMA DE TICKETS v1.0.0
-- =========================================
--
-- Este script mejora el sistema de estadísticas:
-- ✅ Elimina tabla simple anterior
-- ✅ Crea estructura completa de reportes
-- ✅ Métricas detalladas por período
-- ✅ Rendimiento de técnicos
-- ✅ Análisis de incidentes
-- ✅ Dashboards ejecutivos
-- =========================================

USE apiticket;

-- Desactivar foreign key checks durante actualización
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- ELIMINAR TABLA ANTERIOR
-- =========================================

DROP TABLE IF EXISTS resumen_ticket_mensual;

-- =========================================
-- NUEVAS TABLAS DE ESTADÍSTICAS
-- =========================================

-- TABLA PRINCIPAL DE MÉTRICAS POR PERÍODO
CREATE TABLE estadistica_periodo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,

    -- Métricas de tickets
    tickets_creados INT NOT NULL DEFAULT 0,
    tickets_asignados INT NOT NULL DEFAULT 0,
    tickets_resueltos INT NOT NULL DEFAULT 0,
    tickets_finalizados INT NOT NULL DEFAULT 0,
    tickets_reabiertos INT NOT NULL DEFAULT 0,
    tickets_pendientes INT NOT NULL DEFAULT 0,

    -- Tiempos de resolución (en horas)
    tiempo_promedio_asignacion DECIMAL(10,2) DEFAULT 0,
    tiempo_promedio_resolucion DECIMAL(10,2) DEFAULT 0,
    tiempo_maximo_resolucion DECIMAL(10,2) DEFAULT 0,
    tiempo_minimo_resolucion DECIMAL(10,2) DEFAULT 0,

    -- Métricas de calidad
    tickets_aprobados INT NOT NULL DEFAULT 0,
    tickets_rechazados INT NOT NULL DEFAULT 0,
    porcentaje_aprobacion DECIMAL(5,2) DEFAULT 0,

    -- Métricas de devoluciones
    solicitudes_devolucion INT NOT NULL DEFAULT 0,
    devoluciones_aprobadas INT NOT NULL DEFAULT 0,
    devoluciones_rechazadas INT NOT NULL DEFAULT 0,

    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Índice único por período
    UNIQUE KEY uk_periodo (periodo_tipo, anio, mes, semana, dia, trimestre)
);

-- ESTADÍSTICAS DE RENDIMIENTO DE TÉCNICOS
CREATE TABLE estadistica_tecnico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tecnico INT NOT NULL,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,

    -- Productividad
    tickets_asignados INT NOT NULL DEFAULT 0,
    tickets_completados INT NOT NULL DEFAULT 0,
    tickets_aprobados INT NOT NULL DEFAULT 0,
    tickets_rechazados INT NOT NULL DEFAULT 0,

    -- Tiempos promedio
    tiempo_promedio_resolucion DECIMAL(10,2) DEFAULT 0,
    tickets_dentro_sla INT NOT NULL DEFAULT 0, -- SLA imaginario de 48 horas

    -- Calidad
    porcentaje_aprobacion DECIMAL(5,2) DEFAULT 0,
    marcas_recibidas INT NOT NULL DEFAULT 0,
    fallas_registradas INT NOT NULL DEFAULT 0,

    -- Devoluciones
    solicitudes_devolucion INT NOT NULL DEFAULT 0,
    devoluciones_aprobadas INT NOT NULL DEFAULT 0,

    -- Ranking relativo
    ranking_productividad INT DEFAULT NULL,
    ranking_calidad INT DEFAULT NULL,

    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tecnico_periodo (id_tecnico, periodo_tipo, anio, mes, semana, dia, trimestre)
);

-- ESTADÍSTICAS DE USUARIOS/TRABAJADORES
CREATE TABLE estadistica_trabajador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajador INT NOT NULL,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,

    -- Actividad
    tickets_creados INT NOT NULL DEFAULT 0,
    tickets_finalizados INT NOT NULL DEFAULT 0,
    tickets_reabiertos INT NOT NULL DEFAULT 0,

    -- Evaluaciones realizadas
    evaluaciones_aprobadas INT NOT NULL DEFAULT 0,
    evaluaciones_rechazadas INT NOT NULL DEFAULT 0,

    -- Tiempo promedio desde creación hasta finalización
    tiempo_promedio_ciclo_completo DECIMAL(10,2) DEFAULT 0,

    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_trabajador) REFERENCES trabajador(id) ON DELETE CASCADE,
    UNIQUE KEY uk_trabajador_periodo (id_trabajador, periodo_tipo, anio, mes, semana, dia, trimestre)
);

-- ANÁLISIS DE INCIDENTES Y TENDENCIAS
CREATE TABLE estadistica_incidentes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    periodo_tipo ENUM('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL') NOT NULL,
    anio INT NOT NULL,
    mes INT NULL,
    semana INT NULL,
    dia INT NULL,
    trimestre INT NULL,

    -- Tipos de incidentes
    total_marcas INT NOT NULL DEFAULT 0,
    total_fallas INT NOT NULL DEFAULT 0,
    tecnicos_con_incidentes INT NOT NULL DEFAULT 0,

    -- Patrones
    patron_dia_semana VARCHAR(20) NULL, -- Día con más incidentes
    patron_hora_pico INT NULL, -- Hora con más creaciones de tickets

    -- Tendencias
    tendencia_creacion ENUM('SUBIENDO', 'BAJANDO', 'ESTABLE') DEFAULT 'ESTABLE',
    tendencia_resolucion ENUM('SUBIENDO', 'BAJANDO', 'ESTABLE') DEFAULT 'ESTABLE',

    -- Eficiencia general
    ratio_resolucion DECIMAL(5,2) DEFAULT 0, -- tickets_resueltos / tickets_creados
    ratio_calidad DECIMAL(5,2) DEFAULT 0, -- tickets_aprobados / tickets_evaluados

    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_incidentes_periodo (periodo_tipo, anio, mes, semana, dia, trimestre)
);

-- MÉTRICAS EJECUTIVAS (DASHBOARDS)
CREATE TABLE dashboard_ejecutivo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reporte DATE NOT NULL,

    -- KPIs principales
    total_tickets_activos INT NOT NULL DEFAULT 0,
    total_tickets_vencidos INT NOT NULL DEFAULT 0, -- Más de 72 horas sin resolver
    total_tecnicos_activos INT NOT NULL DEFAULT 0,
    total_trabajadores_activos INT NOT NULL DEFAULT 0,

    -- Métricas de rendimiento
    sla_cumplimiento DECIMAL(5,2) DEFAULT 0, -- % de tickets resueltos en tiempo
    satisfaccion_promedio DECIMAL(3,2) DEFAULT 0, -- % de aprobación
    tiempo_resolucion_promedio DECIMAL(10,2) DEFAULT 0,

    -- Alertas y problemas
    tickets_criticos INT NOT NULL DEFAULT 0,
    tecnicos_con_problemas INT NOT NULL DEFAULT 0, -- Más de 3 marcas/fallas
    backlog_pendiente INT NOT NULL DEFAULT 0,

    -- Tendencias (comparación con período anterior)
    variacion_tickets_creados DECIMAL(5,2) DEFAULT 0, -- % cambio vs período anterior
    variacion_resolucion DECIMAL(5,2) DEFAULT 0,
    variacion_calidad DECIMAL(5,2) DEFAULT 0,

    -- Predicciones (basadas en tendencias)
    prediccion_tickets_proxima_semana INT DEFAULT NULL,
    prediccion_carga_trabajo ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') DEFAULT 'MEDIA',

    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_dashboard_fecha (fecha_reporte)
);

-- CACHE DE CONSULTAS FRECUENTES
CREATE TABLE cache_consultas (
    id VARCHAR(100) PRIMARY KEY, -- Identificador único de la consulta
    nombre_consulta VARCHAR(200) NOT NULL,
    resultado_json JSON NOT NULL, -- Resultado en formato JSON
    parametros_json JSON NULL, -- Parámetros usados
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,

    INDEX idx_cache_nombre (nombre_consulta),
    INDEX idx_cache_expiracion (fecha_expiracion)
);

-- =========================================
-- ÍNDICES OPTIMIZADOS PARA REPORTES
-- =========================================

-- Estadísticas por período
CREATE INDEX idx_estadistica_periodo_tipo ON estadistica_periodo(periodo_tipo, anio, mes);
CREATE INDEX idx_estadistica_fecha ON estadistica_periodo(fecha_calculo);

-- Estadísticas de técnicos
CREATE INDEX idx_estadistica_tecnico_periodo ON estadistica_tecnico(id_tecnico, periodo_tipo, anio, mes);
CREATE INDEX idx_estadistica_tecnico_ranking ON estadistica_tecnico(ranking_productividad, ranking_calidad);

-- Estadísticas de trabajadores
CREATE INDEX idx_estadistica_trabajador_periodo ON estadistica_trabajador(id_trabajador, periodo_tipo, anio, mes);

-- Análisis de incidentes
CREATE INDEX idx_estadistica_incidentes_periodo ON estadistica_incidentes(periodo_tipo, anio, mes);
CREATE INDEX idx_estadistica_tendencia ON estadistica_incidentes(tendencia_creacion, tendencia_resolucion);

-- Dashboard ejecutivo
CREATE INDEX idx_dashboard_fecha ON dashboard_ejecutivo(fecha_reporte);

-- =========================================
-- VISTAS ÚTILES PARA REPORTES RÁPIDOS
-- =========================================

-- Vista de resumen actual
CREATE OR REPLACE VIEW vista_resumen_actual AS
SELECT
    COUNT(CASE WHEN estado = 'NO_ATENDIDO' THEN 1 END) as tickets_no_atendidos,
    COUNT(CASE WHEN estado = 'ATENDIDO' THEN 1 END) as tickets_en_proceso,
    COUNT(CASE WHEN estado = 'RESUELTO' THEN 1 END) as tickets_resueltos,
    COUNT(CASE WHEN estado = 'FINALIZADO' THEN 1 END) as tickets_finalizados,
    COUNT(CASE WHEN estado = 'REABIERTO' THEN 1 END) as tickets_reabiertos,
    COUNT(*) as total_tickets,
    AVG(TIMESTAMPDIFF(HOUR, fecha_creacion, COALESCE(fecha_ultima_actualizacion, NOW()))) as tiempo_promedio_horas
FROM ticket;

-- Vista de rendimiento de técnicos (último mes)
CREATE OR REPLACE VIEW vista_rendimiento_tecnicos AS
SELECT
    t.id,
    u.nombre,
    u.apellido,
    t.fallas,
    t.marcas,
    COUNT(tpt.id) as tickets_trabajados,
    COUNT(CASE WHEN tpt.estado_final = 'FINALIZADO' THEN 1 END) as tickets_completados,
    ROUND(
        COUNT(CASE WHEN tpt.estado_final = 'FINALIZADO' THEN 1 END) * 100.0 /
        NULLIF(COUNT(tpt.id), 0), 2
    ) as porcentaje_completados
FROM tecnico t
JOIN usuario u ON t.id = u.id
LEFT JOIN tecnico_por_ticket tpt ON t.id = tpt.id_tecnico
    AND tpt.fecha_asignacion >= DATE_SUB(NOW(), INTERVAL 30 DAY)
WHERE u.activo = TRUE
GROUP BY t.id, u.nombre, u.apellido, t.fallas, t.marcas;

-- Vista de tickets por trabajador (último mes)
CREATE OR REPLACE VIEW vista_actividad_trabajadores AS
SELECT
    tr.id,
    u.nombre,
    u.apellido,
    COUNT(tk.id) as tickets_creados,
    COUNT(CASE WHEN tk.estado = 'FINALIZADO' THEN 1 END) as tickets_finalizados,
    COUNT(CASE WHEN tk.estado = 'REABIERTO' THEN 1 END) as tickets_reabiertos,
    AVG(TIMESTAMPDIFF(HOUR, tk.fecha_creacion, tk.fecha_ultima_actualizacion)) as tiempo_promedio_resolucion
FROM trabajador tr
JOIN usuario u ON tr.id = u.id
LEFT JOIN ticket tk ON tr.id = tk.id_creador
    AND tk.fecha_creacion >= DATE_SUB(NOW(), INTERVAL 30 DAY)
WHERE u.activo = TRUE
GROUP BY tr.id, u.nombre, u.apellido;

-- Reactivar foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- PROCEDIMIENTOS PARA CÁLCULO AUTOMÁTICO
-- =========================================

DELIMITER //

-- Procedimiento para calcular estadísticas diarias
CREATE PROCEDURE CalcularEstadisticasDiarias(IN fecha_calculo DATE)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Insertar estadísticas del período
    INSERT INTO estadistica_periodo (
        periodo_tipo, anio, mes, dia,
        tickets_creados, tickets_resueltos, tickets_finalizados, tickets_reabiertos
    )
    SELECT
        'DIARIO',
        YEAR(fecha_calculo),
        MONTH(fecha_calculo),
        DAY(fecha_calculo),
        COUNT(CASE WHEN DATE(fecha_creacion) = fecha_calculo THEN 1 END),
        COUNT(CASE WHEN DATE(fecha_ultima_actualizacion) = fecha_calculo AND estado = 'RESUELTO' THEN 1 END),
        COUNT(CASE WHEN DATE(fecha_ultima_actualizacion) = fecha_calculo AND estado = 'FINALIZADO' THEN 1 END),
        COUNT(CASE WHEN DATE(fecha_ultima_actualizacion) = fecha_calculo AND estado = 'REABIERTO' THEN 1 END)
    FROM ticket
    ON DUPLICATE KEY UPDATE
        tickets_creados = VALUES(tickets_creados),
        tickets_resueltos = VALUES(tickets_resueltos),
        tickets_finalizados = VALUES(tickets_finalizados),
        tickets_reabiertos = VALUES(tickets_reabiertos),
        ultima_actualizacion = NOW();

    COMMIT;
END//

-- Procedimiento para calcular ranking de técnicos
CREATE PROCEDURE CalcularRankingTecnicos()
BEGIN
    UPDATE estadistica_tecnico et1
    SET ranking_productividad = (
        SELECT COUNT(*) + 1
        FROM estadistica_tecnico et2
        WHERE et2.tickets_completados > et1.tickets_completados
        AND et2.periodo_tipo = 'MENSUAL'
        AND et2.anio = YEAR(NOW())
        AND et2.mes = MONTH(NOW())
    ),
    ranking_calidad = (
        SELECT COUNT(*) + 1
        FROM estadistica_tecnico et2
        WHERE et2.porcentaje_aprobacion > et1.porcentaje_aprobacion
        AND et2.periodo_tipo = 'MENSUAL'
        AND et2.anio = YEAR(NOW())
        AND et2.mes = MONTH(NOW())
    )
    WHERE et1.periodo_tipo = 'MENSUAL'
    AND et1.anio = YEAR(NOW())
    AND et1.mes = MONTH(NOW());
END//

DELIMITER ;

-- =========================================
-- DATOS DE EJEMPLO PARA TESTING
-- =========================================

-- Insertar algunas estadísticas de ejemplo para el mes actual
INSERT INTO estadistica_periodo (
    periodo_tipo, anio, mes, tickets_creados, tickets_resueltos,
    tickets_finalizados, porcentaje_aprobacion
) VALUES
('MENSUAL', YEAR(NOW()), MONTH(NOW()), 45, 38, 35, 92.11);

-- =========================================
-- VERIFICACIÓN DE LA NUEVA ESTRUCTURA
-- =========================================

SELECT
    'Sistema de estadísticas actualizado correctamente' as status,
    (SELECT COUNT(*) FROM information_schema.TABLES
     WHERE TABLE_SCHEMA = 'apiticket'
     AND TABLE_NAME LIKE 'estadistica%' OR TABLE_NAME LIKE 'dashboard%' OR TABLE_NAME LIKE 'cache%') as tablas_estadisticas,
    (SELECT COUNT(*) FROM information_schema.VIEWS
     WHERE TABLE_SCHEMA = 'apiticket'
     AND TABLE_NAME LIKE 'vista_%') as vistas_creadas,
    CURRENT_TIMESTAMP as fecha_actualizacion;
