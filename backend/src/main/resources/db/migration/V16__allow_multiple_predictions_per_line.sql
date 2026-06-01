CREATE TABLE predictions_new (
    id              INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER  NOT NULL REFERENCES users(id),
    betting_line_id INTEGER  NOT NULL REFERENCES betting_lines(id),
    match_id        INTEGER  NOT NULL REFERENCES matches(id),
    credit_bet      INTEGER  NOT NULL CHECK(credit_bet > 0),
    credit_result   INTEGER,
    status          TEXT     NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO predictions_new (
    id,
    user_id,
    betting_line_id,
    match_id,
    credit_bet,
    credit_result,
    status,
    created_at
)
SELECT
    id,
    user_id,
    betting_line_id,
    match_id,
    credit_bet,
    credit_result,
    status,
    created_at
FROM predictions;

DROP TABLE predictions;
ALTER TABLE predictions_new RENAME TO predictions;

CREATE INDEX IF NOT EXISTS idx_predictions_user ON predictions(user_id);
CREATE INDEX IF NOT EXISTS idx_predictions_match ON predictions(match_id);
