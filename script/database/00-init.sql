-- =============================================
-- apiTickets — esquema inicial
--
-- Motor: MySQL 8.0 (el que corre docker-compose y el único driver del pom).
-- PostgreSQL 16 es el objetivo documentado, todavía no el estado actual: cuando se migre,
-- este archivo hay que reescribirlo (ENUM, binary(16) y engine=InnoDB son de MySQL).
--
-- Los tipos de la tabla `users` NO son arbitrarios: son exactamente los que genera
-- Hibernate 7.4.5 desde la entidad `User` con MySQLDialect. Importa porque
-- `spring.jpa.hibernate.ddl-auto=update` está activo y va a intentar reconciliar cualquier
-- diferencia. En particular:
--   * UUID  -> binary(16)   (NO char(36))
--   * LocalDateTime -> datetime(6)
--   * enum de Java -> ENUM nativo de MySQL
--
-- Este script se monta en /docker-entrypoint-initdb.d, así que corre sobre la base que
-- define MYSQL_DATABASE en docker-compose.yml. No hace CREATE DATABASE ni USE a propósito:
-- así funciona con cualquier nombre de base. Para correrlo a mano, descomentá:
--
--   CREATE DATABASE IF NOT EXISTS apiticket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--   USE apiticket;
--
-- Las tablas del módulo ticket están en 10-ticket-pending.sql y NO se aplican todavía
-- (ver el encabezado de ese archivo).
-- =============================================

-- =============================================
-- DOMINIO: USERS
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id            binary(16)   NOT NULL,
    first_name    varchar(255) NOT NULL,
    -- last_name y phone son NULL-ables: la entidad no los marca obligatorios y el alta
    -- (AuthService.register -> UserApi.create) no setea el teléfono.
    last_name     varchar(255) NULL,
    phone         varchar(255) NULL,
    -- 255 y no 50: un hash BCrypt ocupa 60 caracteres y se truncaría.
    email         varchar(255) NOT NULL,
    password_hash varchar(255) NOT NULL,
    global_role   enum('SUPERADMIN','ADMIN','USER')                                    NOT NULL DEFAULT 'USER',
    status        enum('PENDING_VERIFICATION','ACTIVE','INACTIVE','SUSPENDED','DELETED') NOT NULL DEFAULT 'PENDING_VERIFICATION',
    -- Los llena @PrePersist/@PreUpdate de la entidad, por eso no tienen DEFAULT.
    created_at    datetime(6)  NOT NULL,
    updated_at    datetime(6)  NOT NULL,
    -- Baja lógica: fuente de verdad de todas las consultas del repositorio.
    deleted_at    datetime(6)  NULL DEFAULT NULL,

    PRIMARY KEY (id),
    -- El UNIQUE aplica a la tabla completa, incluidos los dados de baja: un usuario con
    -- deleted_at sigue ocupando su email y no se puede reutilizar. De ahí que
    -- UserRepository.existsByEmail deliberadamente no filtre por deleted_at.
    UNIQUE KEY uk_users_email (email),

    -- Índices alineados con las consultas reales de UserRepository. El orden de columnas
    -- pone primero la discriminante y deja deleted_at como filtro acompañante.
    KEY idx_users_status_deleted     (status, deleted_at),
    KEY idx_users_global_role_deleted (global_role, deleted_at),
    KEY idx_users_deleted_at         (deleted_at)
    -- searchActiveByName usa LIKE '%term%', que no puede aprovechar un índice B-tree.
    -- Si la búsqueda por nombre crece, la solución es un índice FULLTEXT, no uno más acá.
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- DOMINIO: AUTH
--
-- Mapeadas por module/auth/model/AuthToken y RefreshToken. Los tipos salen de lo que
-- genera Hibernate 7 para esas entidades; verificado con hbm2ddl=validate.
-- Las dos guardan SHA-256 del token, nunca el valor.
-- =============================================

-- token_hash guarda SHA-256 hex de (user_id + ':' + código), no del código solo: un código
-- de 6 dígitos tiene un millón de valores y dos usuarios podrían colisionar contra el UNIQUE.
CREATE TABLE IF NOT EXISTS auth_tokens (
    id         binary(16)  NOT NULL,
    user_id    binary(16)  NOT NULL,
    token_type enum('VERIFICATION','PASSWORD_RESET') NOT NULL,
    token_hash varchar(64) NOT NULL,
    expires_at datetime(6) NOT NULL,
    -- bit(1) y no boolean: Hibernate genera `bit` para un boolean de Java, y con
    -- ddl-auto=update una diferencia de tipo la reconcilia por su cuenta.
    used       bit(1)      NOT NULL DEFAULT b'0',
    -- Intentos fallidos del código. Sin esta columna el máximo de 3 intentos que define
    -- next_steps.md:1039 no tiene dónde persistirse.
    attempts   smallint    NOT NULL DEFAULT 0,
    created_at datetime(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_tokens_hash (token_hash),
    -- MySQL no tiene índices parciales (el `WHERE used = FALSE` de la versión anterior es
    -- sintaxis de PostgreSQL). Se incluye `used` como tercera columna en su lugar.
    KEY idx_auth_tokens_user_type_used (user_id, token_type, used),
    KEY idx_auth_tokens_expires_at (expires_at),

    CONSTRAINT fk_auth_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         binary(16)  NOT NULL,
    user_id    binary(16)  NOT NULL,
    token_hash varchar(64) NOT NULL,
    expires_at datetime(6) NOT NULL,
    revoked_at datetime(6) NULL DEFAULT NULL,
    created_at datetime(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_hash (token_hash),
    -- Un token está vigente si revoked_at IS NULL y expires_at > now(). No hay columna
    -- is_active: era redundante con revoked_at y quedaba fácilmente desincronizada.
    KEY idx_refresh_tokens_user_revoked (user_id, revoked_at),
    KEY idx_refresh_tokens_expires_at (expires_at),

    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- VERSIÓN DEL ESQUEMA
-- =============================================

CREATE TABLE IF NOT EXISTS schema_version (
    version     varchar(20) NOT NULL,
    description text,
    applied_at  datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO schema_version (version, description) VALUES
    ('2.0.0', 'users unificado (entidad unica + global_role, ids UUID) y dominio auth. Modulo ticket pendiente.')
ON DUPLICATE KEY UPDATE description = VALUES(description);
