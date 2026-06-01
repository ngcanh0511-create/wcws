CREATE TABLE predictions (
    id              INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER  NOT NULL REFERENCES users(id),
    betting_line_id INTEGER  NOT NULL REFERENCES betting_lines(id),
    match_id        INTEGER  NOT NULL REFERENCES matches(id),
    credit_bet      INTEGER  NOT NULL CHECK(credit_bet > 0),
    credit_result   INTEGER,
    status          TEXT     NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, betting_line_id)
);
