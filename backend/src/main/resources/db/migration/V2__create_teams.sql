CREATE TABLE teams (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,
    short_name  TEXT    NOT NULL,
    flag_url    TEXT,
    group_name  TEXT,
    external_id TEXT
);
