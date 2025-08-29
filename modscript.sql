CREATE TABLE solicitud_devolucion(
    id SERIAL PRIMARY KEY,
    id_tecnico INT NOT NULL,
    id_ticket INT NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    estado ENUM('pendiente', 'aprobado', 'rechazado') DEFAULT 'pendiente',
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,
    id_admin_resolutor INT NULL,
    comentario_resolucion VARCHAR(200) NULL,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id),
    FOREIGN KEY (id_ticket) REFERENCES ticket(id),
    FOREIGN KEY (id_admin_resolutor) REFERENCES administrador(id)
);