CREATE TABLE users (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    username      TEXT     NOT NULL UNIQUE,
    password_hash TEXT     NOT NULL,
    display_name  TEXT     NOT NULL,
    avatar_path   TEXT,
    credits       INTEGER  NOT NULL DEFAULT 1000,
    role          TEXT     NOT NULL DEFAULT 'USER',
    is_locked     INTEGER  NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
