CREATE TABLE tournament_predictions (
    id              INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER  NOT NULL REFERENCES users(id),
    prediction_type TEXT     NOT NULL,
    team_id         INTEGER  REFERENCES teams(id),
    player_name     TEXT,
    credit_bet      INTEGER  NOT NULL CHECK(credit_bet > 0),
    credit_result   INTEGER,
    status          TEXT     NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, prediction_type)
);
