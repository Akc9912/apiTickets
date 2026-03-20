-- ============================================
-- Database: tickets_system
-- Version: 1.0.0 (Current Structure)
-- Description: Base de datos para el sistema de tickets con arquitectura modular
-- Created: 2026-02-01
-- ============================================

-- Eliminar base de datos si existe (¡CUIDADO EN PRODUCCIÓN!)
DROP DATABASE IF EXISTS tickets_system;

-- Crear base de datos
CREATE DATABASE tickets_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tickets_system;

-- ============================================
-- MODULE: USER
-- Tablas relacionadas con usuarios y roles
-- ============================================

-- Tabla base: User (con herencia JOINED)
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_role VARCHAR(20) NOT NULL COMMENT 'Discriminador para herencia: ADMIN, SUPERADMIN, DEVELOPER, SUPPORT',
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'Rol del usuario: ADMIN, SUPERADMIN, DEVELOPER, SUPPORT',
    change_password BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: Admin (hereda de User)
CREATE TABLE admin (
    id INT PRIMARY KEY,
    
    CONSTRAINT fk_admin_user FOREIGN KEY (id) 
        REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: SuperAdmin (hereda de User)
CREATE TABLE superadmin (
    id INT PRIMARY KEY,
    
    CONSTRAINT fk_superadmin_user FOREIGN KEY (id) 
        REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: Support (hereda de User)
CREATE TABLE support (
    id INT PRIMARY KEY,
    
    CONSTRAINT fk_support_user FOREIGN KEY (id) 
        REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: Developer (hereda de User)
CREATE TABLE developer (
    id INT PRIMARY KEY,
    warnings INT DEFAULT 0 COMMENT 'Número de advertencias',
    failures INT DEFAULT 0 COMMENT 'Número de fallos',
    
    CONSTRAINT fk_developer_user FOREIGN KEY (id) 
        REFERENCES user(id) ON DELETE CASCADE,
    
    INDEX idx_warnings (warnings),
    INDEX idx_failures (failures)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- MODULE: TICKET
-- Tablas relacionadas con tickets
-- ============================================

-- Tabla: Ticket
CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
        COMMENT 'Estados: PENDING, IN_PROGRESS, RESOLVED, CLOSED, REOPENED',
    creator_id INT NOT NULL,
    developer_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ticket_creator FOREIGN KEY (creator_id) 
        REFERENCES user(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_developer FOREIGN KEY (developer_id) 
        REFERENCES developer(id) ON DELETE SET NULL,
    
    INDEX idx_status (status),
    INDEX idx_creator (creator_id),
    INDEX idx_developer (developer_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: TicketRefundRequest (Solicitudes de devolución)
CREATE TABLE ticket_refund_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    developer_id INT NOT NULL,
    ticket_id INT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
        COMMENT 'Estados: PENDING, APPROVED, REJECTED',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolution_date TIMESTAMP NULL,
    resolved_by_id INT NULL COMMENT 'Admin que resolvió la solicitud',
    resolution_comment VARCHAR(500) NULL,
    
    CONSTRAINT fk_refund_developer FOREIGN KEY (developer_id) 
        REFERENCES developer(id) ON DELETE CASCADE,
    CONSTRAINT fk_refund_ticket FOREIGN KEY (ticket_id) 
        REFERENCES ticket(id) ON DELETE CASCADE,
    CONSTRAINT fk_refund_resolved_by FOREIGN KEY (resolved_by_id) 
        REFERENCES admin(id) ON DELETE SET NULL,
    
    INDEX idx_status (status),
    INDEX idx_developer (developer_id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_request_date (request_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: TicketEvaluationHistory (Historial de evaluaciones)
CREATE TABLE ticket_evaluation_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT 'Usuario que evalúa',
    ticket_id INT NOT NULL,
    was_approved BOOLEAN NOT NULL,
    comments TEXT NULL,
    evaluation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_evaluation_user FOREIGN KEY (user_id) 
        REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_evaluation_ticket FOREIGN KEY (ticket_id) 
        REFERENCES ticket(id) ON DELETE CASCADE,
    
    INDEX idx_ticket (ticket_id),
    INDEX idx_user (user_id),
    INDEX idx_evaluation_date (evaluation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: DeveloperByTicket (Historial de asignaciones)
CREATE TABLE developer_by_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    developer_id INT NOT NULL,
    initial_status VARCHAR(20) NOT NULL,
    final_status VARCHAR(20) NULL,
    comment TEXT NULL,
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unassignment_date TIMESTAMP NULL,
    
    CONSTRAINT fk_dev_ticket_ticket FOREIGN KEY (ticket_id) 
        REFERENCES ticket(id) ON DELETE CASCADE,
    CONSTRAINT fk_dev_ticket_developer FOREIGN KEY (developer_id) 
        REFERENCES developer(id) ON DELETE CASCADE,
    
    INDEX idx_ticket (ticket_id),
    INDEX idx_developer (developer_id),
    INDEX idx_assignment_date (assignment_date),
    INDEX idx_active_assignments (developer_id, unassignment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: DeveloperIncident (Incidentes de developers)
CREATE TABLE developer_incident (
    id INT AUTO_INCREMENT PRIMARY KEY,
    developer_id INT NOT NULL,
    ticket_id INT NOT NULL,
    incident VARCHAR(50) NOT NULL 
        COMMENT 'Tipos: WARNING, FAILURE',
    reason TEXT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_incident_developer FOREIGN KEY (developer_id) 
        REFERENCES developer(id) ON DELETE CASCADE,
    CONSTRAINT fk_incident_ticket FOREIGN KEY (ticket_id) 
        REFERENCES ticket(id) ON DELETE CASCADE,
    
    INDEX idx_developer (developer_id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_incident_type (incident),
    INDEX idx_registration_date (registration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista: Tickets activos con información del developer
CREATE VIEW v_active_tickets AS
SELECT 
    t.id,
    t.title,
    t.description,
    t.status,
    CONCAT(u_creator.name, ' ', u_creator.last_name) AS creator_name,
    u_creator.email AS creator_email,
    CONCAT(u_dev.name, ' ', u_dev.last_name) AS developer_name,
    u_dev.email AS developer_email,
    t.created_at,
    t.updated_at
FROM ticket t
INNER JOIN user u_creator ON t.creator_id = u_creator.id
LEFT JOIN developer d ON t.developer_id = d.id
LEFT JOIN user u_dev ON d.id = u_dev.id
WHERE t.status NOT IN ('CLOSED');

-- Vista: Estadísticas de developers
CREATE VIEW v_developer_stats AS
SELECT 
    d.id,
    CONCAT(u.name, ' ', u.last_name) AS developer_name,
    u.email,
    d.warnings,
    d.failures,
    COUNT(DISTINCT dbt.ticket_id) AS total_tickets_handled,
    COUNT(DISTINCT CASE WHEN dbt.unassignment_date IS NULL THEN dbt.ticket_id END) AS active_tickets,
    COUNT(DISTINCT di.id) AS total_incidents
FROM developer d
INNER JOIN user u ON d.id = u.id
LEFT JOIN developer_by_ticket dbt ON d.id = dbt.developer_id
LEFT JOIN developer_incident di ON d.id = di.developer_id
WHERE u.active = TRUE
GROUP BY d.id, u.name, u.last_name, u.email, d.warnings, d.failures;

-- Vista: Solicitudes de devolución pendientes
CREATE VIEW v_pending_refund_requests AS
SELECT 
    trr.id,
    trr.reason,
    trr.request_date,
    t.id AS ticket_id,
    t.title AS ticket_title,
    CONCAT(u_dev.name, ' ', u_dev.last_name) AS developer_name,
    u_dev.email AS developer_email
FROM ticket_refund_requests trr
INNER JOIN ticket t ON trr.ticket_id = t.id
INNER JOIN developer d ON trr.developer_id = d.id
INNER JOIN user u_dev ON d.id = u_dev.id
WHERE trr.status = 'PENDING'
ORDER BY trr.request_date DESC;

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- ============================================

DELIMITER //

-- Procedimiento: Asignar ticket a developer
CREATE PROCEDURE sp_assign_ticket_to_developer(
    IN p_ticket_id INT,
    IN p_developer_id INT,
    IN p_comment TEXT
)
BEGIN
    DECLARE v_current_status VARCHAR(20);
    
    -- Obtener estado actual del ticket
    SELECT status INTO v_current_status FROM ticket WHERE id = p_ticket_id;
    
    -- Actualizar ticket
    UPDATE ticket 
    SET developer_id = p_developer_id,
        status = 'IN_PROGRESS',
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_ticket_id;
    
    -- Registrar en historial
    INSERT INTO developer_by_ticket (ticket_id, developer_id, initial_status, comment)
    VALUES (p_ticket_id, p_developer_id, v_current_status, p_comment);
END //

-- Procedimiento: Cerrar asignación de developer
CREATE PROCEDURE sp_close_developer_assignment(
    IN p_ticket_id INT,
    IN p_developer_id INT,
    IN p_final_status VARCHAR(20),
    IN p_comment TEXT
)
BEGIN
    UPDATE developer_by_ticket
    SET final_status = p_final_status,
        unassignment_date = CURRENT_TIMESTAMP,
        comment = p_comment
    WHERE ticket_id = p_ticket_id 
      AND developer_id = p_developer_id
      AND unassignment_date IS NULL;
END //

DELIMITER ;

-- ============================================
-- INFORMACIÓN DEL ESQUEMA
-- ============================================

-- Versión de la base de datos
CREATE TABLE schema_version (
    version VARCHAR(20) PRIMARY KEY,
    description TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO schema_version (version, description) 
VALUES ('1.0.0', 'Estructura base del sistema - Módulos: auth, user, ticket');

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
