CREATE TABLE system_config (
    key         TEXT     PRIMARY KEY,
    value       TEXT     NOT NULL,
    description TEXT     NOT NULL,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
