CREATE TABLE achievements (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    code            TEXT    NOT NULL UNIQUE,
    name            TEXT    NOT NULL,
    description     TEXT    NOT NULL,
    icon            TEXT    NOT NULL,
    badge_color     TEXT    NOT NULL,
    condition_type  TEXT    NOT NULL,
    condition_value INTEGER,
    is_active       INTEGER NOT NULL DEFAULT 1
);
