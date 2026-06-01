CREATE TABLE activity_feed (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    activity_type TEXT     NOT NULL,
    message       TEXT     NOT NULL,
    reference_id  INTEGER,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
