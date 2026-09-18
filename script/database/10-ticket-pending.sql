-- =============================================
-- apiTickets — módulo ticket (PENDIENTE, NO SE APLICA)
--
-- ⚠️ Este archivo NO se monta en /docker-entrypoint-initdb.d y no debe aplicarse todavía.
--
-- Motivo: las entidades de module/ticket no compilan y su modelo de datos quedó
-- inconsistente con el refactor de `users`. Concretamente:
--
--   1. Ticket, TicketRefundRequest, TicketEvaluationHistory, DeveloperByTicket y
--      DeveloperIncident usan `private int id` con GenerationType.IDENTITY, mientras que
--      `users.id` pasó a ser UUID -> binary(16). Un `int` no puede tener FK a un
--      binary(16): la incompatibilidad de tipos no se arregla en el SQL.
--
--   2. Los @ManyToOne apuntan a las clases `Developer` y `Admin`, que el refactor eliminó.
--      Ya no existen las tablas `developer` ni `admin`, así que las FKs de developer_id y
--      resolved_by_id no tienen destino. Tampoco existen los roles DEVELOPER y SUPPORT en
--      UserRole.
--
-- Por eso las FKs hacia usuarios están comentadas más abajo en lugar de inventadas: emitir
-- DDL que contradiga las entidades sería peor que dejar la decisión explícita. Las tablas
-- de acá se pueden crear (el DDL es válido), pero quedan sin integridad referencial contra
-- `users` hasta que se resuelva la migración.
--
-- Decisión pendiente, en este orden:
--   a. Migrar los ids de ticket de `int` a UUID (coherente con `users`), o dejarlos `int` y
--      referenciar al usuario por una columna `binary(16)` sin relación JPA.
--   b. Reemplazar `Developer`/`Admin` por `User` + chequeo de rol, y decidir qué significa
--      "developer asignado" ahora que no hay subclases.
--   c. Recién entonces descomentar las FKs y renombrar este archivo a 10-ticket.sql.
--
-- Lo que sí se corrigió respecto de la versión anterior de este script:
--   * Nombres de tabla tomados de las anotaciones @Table reales (eran `ticket` y `user`
--     en singular, que no existen: son `tickets` y `users`).
--   * `ENUM("A","B")` con comillas dobles -> comillas simples (lo anterior no era SQL
--     válido salvo con ANSI_QUOTES).
--   * Faltaba una coma tras la definición de `role`.
--   * Tipos alineados con las entidades: datetime(6) en vez de TIMESTAMP, ENUM nativo para
--     los @Enumerated(STRING), TEXT donde la entidad declara columnDefinition = "TEXT".
--   * Se eliminaron las tres vistas y los dos procedimientos almacenados: referenciaban
--     columnas y tablas inexistentes (`u.name`, `u.active`, `d.warnings`, `d.failures`,
--     `developer`, `admin`, `ticket` en singular). Reescribirlos requiere antes (a) y (b).
--   * Se eliminó el DROP DATABASE del encabezado: un script de init no debe borrar la base.
-- =============================================

-- Requiere que 00-init.sql ya haya creado `users`.

-- =============================================
-- Ticket
-- Entidad: module/ticket/model/Ticket.java  (sin @Table -> nombre por defecto: tickets)
-- =============================================

CREATE TABLE IF NOT EXISTS tickets (
    id           int          NOT NULL AUTO_INCREMENT,
    title        varchar(255) NOT NULL,
    description  varchar(255) NOT NULL,
    status       enum('PENDING','IN_PROGRESS','RESOLVED','CLOSED','REOPENED') NOT NULL,
    -- creator_id y developer_id: ver punto 1 del encabezado. Hoy son int y no pueden
    -- referenciar users.id (binary(16)).
    creator_id   int          NOT NULL,
    developer_id int          NULL,
    created_at   datetime(6)  NOT NULL,
    updated_at   datetime(6)  NOT NULL,

    PRIMARY KEY (id),
    KEY idx_tickets_status (status),
    KEY idx_tickets_creator (creator_id),
    KEY idx_tickets_developer (developer_id),
    KEY idx_tickets_created_at (created_at)

    -- , CONSTRAINT fk_tickets_creator   FOREIGN KEY (creator_id)   REFERENCES users (id)
    -- , CONSTRAINT fk_tickets_developer FOREIGN KEY (developer_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TicketRefundRequest — solicitudes de devolución
-- Entidad: module/ticket/model/TicketRefundRequest.java
-- =============================================

CREATE TABLE IF NOT EXISTS ticket_refund_requests (
    id                 int          NOT NULL AUTO_INCREMENT,
    developer_id       int          NOT NULL,
    ticket_id          int          NOT NULL,
    reason             varchar(500) NOT NULL,
    status             enum('PENDING','APPROVED','REJECTED') NOT NULL,
    request_date       datetime(6)  NOT NULL,
    resolution_date    datetime(6)  NULL,
    -- Admin que resolvió la solicitud.
    resolved_by_id     int          NULL,
    resolution_comment varchar(500) NULL,

    PRIMARY KEY (id),
    KEY idx_refund_status (status),
    KEY idx_refund_developer (developer_id),
    KEY idx_refund_ticket (ticket_id),
    KEY idx_refund_request_date (request_date),

    CONSTRAINT fk_refund_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (id) ON DELETE CASCADE

    -- , CONSTRAINT fk_refund_developer   FOREIGN KEY (developer_id)   REFERENCES users (id)
    -- , CONSTRAINT fk_refund_resolved_by FOREIGN KEY (resolved_by_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TicketEvaluationHistory — historial de evaluaciones
-- Entidad: module/ticket/model/TicketEvaluationHistory.java
-- =============================================

CREATE TABLE IF NOT EXISTS ticket_evaluation_history (
    id              int         NOT NULL AUTO_INCREMENT,
    -- Usuario que evalúa.
    user_id         int         NOT NULL,
    ticket_id       int         NOT NULL,
    was_approved    boolean     NOT NULL,
    comments        text        NULL,
    evaluation_date datetime(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_evaluation_ticket (ticket_id),
    KEY idx_evaluation_user (user_id),
    KEY idx_evaluation_date (evaluation_date),

    CONSTRAINT fk_evaluation_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (id) ON DELETE CASCADE

    -- , CONSTRAINT fk_evaluation_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- DeveloperByTicket — historial de asignaciones
-- Entidad: module/ticket/model/DeveloperByTicket.java
-- =============================================

CREATE TABLE IF NOT EXISTS developer_by_ticket (
    id                int         NOT NULL AUTO_INCREMENT,
    ticket_id         int         NOT NULL,
    developer_id      int         NOT NULL,
    initial_status    enum('PENDING','IN_PROGRESS','RESOLVED','CLOSED','REOPENED') NOT NULL,
    final_status      enum('PENDING','IN_PROGRESS','RESOLVED','CLOSED','REOPENED') NULL,
    comment           text        NULL,
    assignment_date   datetime(6) NOT NULL,
    unassignment_date datetime(6) NULL,

    PRIMARY KEY (id),
    KEY idx_dev_ticket_ticket (ticket_id),
    KEY idx_dev_ticket_developer (developer_id),
    KEY idx_dev_ticket_assignment_date (assignment_date),
    -- Asignaciones abiertas: unassignment_date IS NULL.
    KEY idx_dev_ticket_active (developer_id, unassignment_date),

    CONSTRAINT fk_dev_ticket_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (id) ON DELETE CASCADE

    -- , CONSTRAINT fk_dev_ticket_developer FOREIGN KEY (developer_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- DeveloperIncident — incidentes de developers
-- Entidad: module/ticket/model/DeveloperIncident.java
-- =============================================

CREATE TABLE IF NOT EXISTS developer_incident (
    id                int         NOT NULL AUTO_INCREMENT,
    developer_id      int         NOT NULL,
    ticket_id         int         NOT NULL,
    incident          enum('WARNING','FAILURE') NOT NULL,
    reason            text        NULL,
    registration_date datetime(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_incident_developer (developer_id),
    KEY idx_incident_ticket (ticket_id),
    KEY idx_incident_type (incident),
    KEY idx_incident_registration_date (registration_date),

    CONSTRAINT fk_incident_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (id) ON DELETE CASCADE

    -- , CONSTRAINT fk_incident_developer FOREIGN KEY (developer_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
