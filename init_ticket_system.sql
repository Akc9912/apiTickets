CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100)NOT NULL,
    password VARCHAR(100) NOT NULL,
    cambiar_pass BOOLEAN NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL
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
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);



CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    id_estado INT,
    id_creador INT,
    FOREIGN KEY (id_creador) REFERENCES trabajador(id) ON DELETE SET NULL,
    FOREIGN KEY (id_estado) REFERENCES estado_tickets(id) ON DELETE SET NULL
);

CREATE TABLE estado_tickets(
    id VARCHAR(20) PRIMARY KEY
);

CREATE TABLE tecnico_por_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_tecnico INT NOT NULL,
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_desasignacion DATETIME DEFAULT NULL,
    id_estado_inicial VARCHAR(20) NOT NULL,
    id_estado_final VARCHAR(20) NOT null,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE SET NULL,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id) ON DELETE SET NULL,
    FOREIGN KEY (id_estado_inicial) REFERENCES estado_tickets(id) ON DELETE SET NULL,
    FOREIGN KEY (id_estado_final) REFERENCES estado_tickets(id) ON DELETE SET NULL
);

CREATE TABLE cambio_password(
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    password VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE TABLE notificacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo VARCHAR(30) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo) REFERENCES tipo_notificacion(id) ON DELETE SET NULL
);

CREATE TABLE tipo_notificacion (
    id VARCHAR(30) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
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
    id_usuario INT, -- quien ejecutó la acción
    id_accion VARCHAR(50) NOT NULL, -- RESUELTO, REABIERTO, FINALIZADO, ASIGNADO
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL,
    FOREIGN KEY (id_accion) REFERENCES estado_tickets(id) ON DELETE SET NULL
);

CREATE TABLE comentario_ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    id_usuario INT NOT NULL,
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
CREATE INDEX idx_tecnico_ticket ON tecnico_por_ticket(id_ticket, id_tecnico);
CREATE INDEX idx_estado_final ON tecnico_por_ticket(id_estado_final);
