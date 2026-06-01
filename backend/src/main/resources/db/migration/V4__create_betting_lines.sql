CREATE TABLE betting_lines (
    id          INTEGER  PRIMARY KEY AUTOINCREMENT,
    match_id    INTEGER  NOT NULL REFERENCES matches(id),
    line_type   TEXT     NOT NULL,
    description TEXT     NOT NULL,
    odds        REAL     NOT NULL,
    result      TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
