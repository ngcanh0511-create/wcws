CREATE TABLE user_achievements (
    id             INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER  NOT NULL REFERENCES users(id),
    achievement_id INTEGER  NOT NULL REFERENCES achievements(id),
    earned_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    match_id       INTEGER  REFERENCES matches(id),
    UNIQUE(user_id, achievement_id)
);
