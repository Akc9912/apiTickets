CREATE TABLE tickets_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tickets_system;

--- =============================================
--- USER DOMAIN
--- =============================================
CREATE TABLE IF NOT EXISTS users(
    id CHAR(36) NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    global_role ENUM('SUPERADMIN', 'ADMIN', 'USER') DEFAULT 'USER',
    status ENUM('PENDING_VERIFICATION', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') DEFAULT 'PENDING_VERIFICATION'
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- INDICES
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_global_role ON users(global_role);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_updated_at ON users(updated_at);
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users(deleted_at);

--- =============================================
--- AUTH DOMAIN
--- =============================================

CREATE TABLE IF NOT EXISTS auth_tokens(
    id CHAR(36) NOT NULL PRIMARY KEY,
    token_type ENUM('VERIFICATION', 'PASSWORD_RESET'),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id CHAR(36) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--- INDICES
CREATE INDEX IF NOT EXISTS idx_auth_tokens_user_used ON auth_tokens(user_id, used);
CREATE INDEX IF NOT EXISTS idx_auth_tokens_user_type_active ON auth_tokens(user_id,token_type) WHERE used = FALSE;
CREATE INDEX IF NOT EXISTS idx_auth_tokens_expires_at ON auth_tokens(expires_at);

CREATE TABLE IF NOT EXISTS refresh_tokens(
    id CHAR(36) NOT NULL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id CHAR(36) NOT NULL,
    revoked_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--- INDICES
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active_revoked ON refresh_tokens(user_id, is_active, revoked_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_created_at ON refresh_tokens(created_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_updated_at ON refresh_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_active ON refresh_tokens(user_id, is_active) WHERE is_active = TRUE AND revoked_at IS NULL;
