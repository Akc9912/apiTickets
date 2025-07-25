-- =========================================
-- SCRIPT DE INICIALIZACIÓN - SISTEMA DE TICKETS
-- =========================================
-- 
-- Este script crea la estructura completa de la base de datos
-- incluyendo soporte para SuperAdmin desde el inicio.
--
-- CREDENCIALES POR DEFECTO:
-- Email: superadmin@sistema.com
-- Password: secret (cambiar después del primer login)
-- Rol: SUPER_ADMIN
--
-- IMPORTANTE: Cambiar la contraseña del SuperAdmin después del despliegue
-- =========================================

use apiticket;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    cambiar_pass BOOLEAN NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE admin (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE super_admin (
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

CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL, -- Enum EstadoTicket
    id_creador INT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_creador) REFERENCES trabajador(id) ON DELETE SET NULL
);

CREATE TABLE tecnico_por_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_tecnico INT NOT NULL,
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_desasignacion DATETIME DEFAULT NULL,
    estado_inicial VARCHAR(20) NOT NULL, -- Enum EstadoTicket
    estado_final VARCHAR(20) NOT NULL,   -- Enum EstadoTicket
    comentario TEXT,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE CASCADE
);

CREATE TABLE cambio_password (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    password VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

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
    fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo) REFERENCES tipo_notificacion(id) ON DELETE SET NULL
);

CREATE TABLE trabajador_por_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_trabajador INT NOT NULL,
    fecha_confirmacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aprobado BOOLEAN NOT NULL,
    comentario TEXT,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_trabajador) REFERENCES trabajador(id) ON DELETE CASCADE
);

CREATE TABLE log_ticket_evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_usuario INT,
    accion VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE TABLE comentario_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_usuario INT ,
    mensaje TEXT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE TABLE resumen_ticket_mensual (
    anio INT NOT NULL,
    mes INT NOT NULL,
    total_creados INT NOT NULL,
    total_resueltos INT NOT NULL,
    total_reabiertos INT NOT NULL,
    promedio_tiempo_resolucion DECIMAL(6,2),
    PRIMARY KEY (anio, mes)
);

-- INDICES

CREATE UNIQUE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_rol ON usuario(rol);

CREATE INDEX idx_tecnico_ticket ON tecnico_por_ticket(id_ticket, id_tecnico);

CREATE INDEX idx_ticket_estado ON ticket(estado);
CREATE INDEX idx_ticket_creador ON ticket(id_creador);
CREATE INDEX idx_ticket_estado_creador ON ticket(estado, id_creador);
CREATE INDEX idx_ticket_fecha_creacion ON ticket(fecha_creacion);
CREATE INDEX idx_ticket_fecha_actualizacion ON ticket(fecha_ultima_actualizacion);

CREATE INDEX idx_notificacion_usuario ON notificacion(id_usuario);
CREATE INDEX idx_comentario_ticket_ticket ON comentario_ticket(id_ticket);

-- DATOS INICIALES


-- Insertar tipos de notificación básicos
INSERT INTO tipo_notificacion (id, descripcion) VALUES
('TICKET_ASIGNADO', 'Ticket asignado a técnico'),
('TICKET_RESUELTO', 'Ticket marcado como resuelto'),
('TICKET_REABIERTO', 'Ticket reabierto'),
('USUARIO_BLOQUEADO', 'Usuario bloqueado por el sistema'),
('PASSWORD_RESET', 'Contraseña reseteada por administrador');

-- Mostrar estadísticas iniciales
SELECT 
    'Configuración inicial completada' as status,
    (SELECT COUNT(*) FROM usuario) as total_usuarios,
    (SELECT COUNT(*) FROM super_admin) as super_admins,
    (SELECT COUNT(*) FROM tipo_notificacion) as tipos_notificacion;
