CREATE TABLE credit_transactions (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    amount        INTEGER  NOT NULL,
    balance_after INTEGER  NOT NULL,
    type          TEXT     NOT NULL,
    reference_id  INTEGER,
    description   TEXT     NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
