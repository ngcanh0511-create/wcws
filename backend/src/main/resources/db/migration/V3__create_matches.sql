CREATE TABLE matches (
    id                   INTEGER  PRIMARY KEY AUTOINCREMENT,
    external_id          TEXT     UNIQUE,
    home_team_id         INTEGER  NOT NULL REFERENCES teams(id),
    away_team_id         INTEGER  NOT NULL REFERENCES teams(id),
    match_date           DATETIME NOT NULL,
    stage                TEXT     NOT NULL,
    home_score           INTEGER,
    away_score           INTEGER,
    status               TEXT     NOT NULL DEFAULT 'SCHEDULED',
    prediction_locked_at DATETIME,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
