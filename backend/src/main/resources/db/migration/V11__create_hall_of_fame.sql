CREATE TABLE hall_of_fame (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    season        TEXT     NOT NULL,
    final_rank    INTEGER  NOT NULL,
    final_credits INTEGER  NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
