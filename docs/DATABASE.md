# Database Design - World Cup Prediction League

## Công nghệ
- **Database:** SQLite
- **Migration:** Flyway
- **ORM:** Spring Data JPA (Hibernate)

---

## ERD (Entity Relationship Diagram)

```
┌──────────────┐       ┌──────────────────────┐       ┌──────────────┐
│    users     │       │       matches         │       │    teams     │
├──────────────┤       ├──────────────────────┤       ├──────────────┤
│ id (PK)      │       │ id (PK)              │  ┌───>│ id (PK)      │
│ username     │       │ external_id          │  │    │ name         │
│ password_hash│       │ home_team_id (FK) ───┼──┘    │ short_name   │
│ display_name │       │ away_team_id (FK) ───┼──┐    │ flag_url     │
│ avatar_path  │       │ match_date           │  └───>│ group_name   │
│ credits      │       │ stage                │       │ external_id  │
│ role         │       │ home_score           │       └──────────────┘
│ is_locked    │       │ away_score           │
│ created_at   │       │ status               │
│ updated_at   │       │ prediction_locked_at │
└──────┬───────┘       └──────────┬───────────┘
       │                          │ has many
       │                          ▼
       │               ┌──────────────────┐
       │               │  betting_lines   │
       │               ├──────────────────┤
       │               │ id (PK)          │
       │               │ match_id (FK)    │
       │               │ line_type        │
       │               │ description      │
       │               │ odds             │
       │               │ result           │
       │               │ created_at       │
       │               └────────┬─────────┘
       │                        │ bet on
       │               ┌────────▼─────────┐
       ├──────────────>│   predictions    │
       │               ├──────────────────┤
       │               │ id (PK)          │
       │               │ user_id (FK)     │
       │               │ betting_line_id  │
       │               │ match_id (FK)    │
       │               │ credit_bet       │
       │               │ credit_result    │
       │               │ status           │
       │               │ created_at       │
       │               └──────────────────┘
       │
       ├──────────────>┌───────────────────────┐
       │               │  credit_transactions  │
       │               ├───────────────────────┤
       │               │ id (PK)               │
       │               │ user_id (FK)          │
       │               │ amount (+ or -)       │
       │               │ balance_after         │
       │               │ type                  │
       │               │ reference_id          │
       │               │ description           │
       │               │ created_at            │
       │               └───────────────────────┘
       │
       ├──────────────>┌──────────────────┐     ┌──────────────────┐
       │               │ user_achievements│     │  achievements    │
       │               ├──────────────────┤     ├──────────────────┤
       │               │ id (PK)          │     │ id (PK)          │
       │               │ user_id (FK)     │     │ code (UNIQUE)    │
       │               │ achievement_id ──┼────>│ name             │
       │               │ earned_at        │     │ description      │
       │               │ match_id         │     │ icon             │
       │               └──────────────────┘     │ badge_color      │
       │                                         │ condition_type   │
       │                                         │ condition_value  │
       │                                         │ is_active        │
       │                                         └──────────────────┘
       │
       ├──────────────>┌──────────────────────┐
       │               │ tournament_preds      │
       │               ├──────────────────────┤
       │               │ id (PK)              │
       │               │ user_id (FK)         │
       │               │ prediction_type      │
       │               │ team_id (FK)         │
       │               │ player_name          │
       │               │ credit_bet           │
       │               │ credit_result        │
       │               │ status               │
       │               │ created_at           │
       │               └──────────────────────┘
       │
       ├──────────────>┌──────────────────┐
       │               │  activity_feed   │
       │               ├──────────────────┤
       │               │ id (PK)          │
       │               │ user_id (FK)     │
       │               │ activity_type    │
       │               │ message          │
       │               │ reference_id     │
       │               │ created_at       │
       │               └──────────────────┘
       │
       └──────────────>┌──────────────────┐
                       │  hall_of_fame    │
                       ├──────────────────┤
                       │ id (PK)          │
                       │ user_id (FK)     │
                       │ season           │
                       │ final_rank       │
                       │ final_credits    │
                       │ created_at       │
                       └──────────────────┘

┌──────────────────┐
│  system_config   │
├──────────────────┤
│ key (PK)         │
│ value            │
│ description      │
│ updated_at       │
└──────────────────┘
```

---

## Schema chi tiết

### users
```sql
CREATE TABLE users (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    username      TEXT     NOT NULL UNIQUE,
    password_hash TEXT     NOT NULL,
    display_name  TEXT     NOT NULL,
    avatar_path   TEXT,
    credits       INTEGER  NOT NULL DEFAULT 1000,
    role          TEXT     NOT NULL DEFAULT 'USER',  -- USER | ADMIN
    is_locked     INTEGER  NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### teams
```sql
CREATE TABLE teams (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,
    short_name  TEXT    NOT NULL,   -- VIE, ARG, BRA...
    flag_url    TEXT,
    group_name  TEXT,               -- Group A, B... NULL sau vòng bảng
    external_id TEXT                -- ID từ football-data.org
);
```

### matches
```sql
CREATE TABLE matches (
    id                   INTEGER  PRIMARY KEY AUTOINCREMENT,
    external_id          TEXT     UNIQUE,
    home_team_id         INTEGER  NOT NULL REFERENCES teams(id),
    away_team_id         INTEGER  NOT NULL REFERENCES teams(id),
    match_date           DATETIME NOT NULL,
    stage                TEXT     NOT NULL,  -- GROUP | R16 | QF | SF | FINAL
    home_score           INTEGER,            -- NULL = chưa diễn ra
    away_score           INTEGER,
    status               TEXT     NOT NULL DEFAULT 'SCHEDULED',
                                             -- SCHEDULED | LIVE | FINISHED | CANCELLED
    prediction_locked_at DATETIME,           -- = match_date - lock_minutes_before_match
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### betting_lines
```sql
CREATE TABLE betting_lines (
    id          INTEGER  PRIMARY KEY AUTOINCREMENT,
    match_id    INTEGER  NOT NULL REFERENCES matches(id),
    line_type   TEXT     NOT NULL,
                -- WIN_HOME | WIN_AWAY | DRAW | OVER | UNDER
                -- BTTS | EXACT_SCORE | FIRST_SCORER | SPECIAL
    description TEXT     NOT NULL,  -- "Argentina Win", "Over 2.5 Goals"
    odds        REAL     NOT NULL,  -- 1.8, 3.2, 5.0...
    result      TEXT,               -- WIN | LOSE | VOID (NULL trước khi kết thúc)
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### predictions
```sql
CREATE TABLE predictions (
    id              INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER  NOT NULL REFERENCES users(id),
    betting_line_id INTEGER  NOT NULL REFERENCES betting_lines(id),
    match_id        INTEGER  NOT NULL REFERENCES matches(id),
    credit_bet      INTEGER  NOT NULL CHECK(credit_bet > 0),
    credit_result   INTEGER,
    -- NULL  = chưa chấm
    -- +180  = thắng, nhận lại 180 credit (bet 100 * odds 1.8)
    -- -100  = thua, ghi nhận đã mất 100 credit khi đặt
    status          TEXT     NOT NULL DEFAULT 'PENDING',  -- PENDING | WON | LOST | VOID
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    -- Cho phép đặt nhiều lần trên cùng một kèo; mỗi lần là một prediction riêng.
);
```

### credit_transactions
```sql
CREATE TABLE credit_transactions (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    amount        INTEGER  NOT NULL,   -- dương = cộng, âm = trừ
    balance_after INTEGER  NOT NULL,
    type          TEXT     NOT NULL,
                  -- BET | WIN | LOSE | ADMIN_TOPUP | INITIAL | VOID_REFUND
    reference_id  INTEGER,            -- prediction_id nếu liên quan trận đấu
    description   TEXT     NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### achievements
```sql
CREATE TABLE achievements (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    code            TEXT    NOT NULL UNIQUE,
    name            TEXT    NOT NULL,
    description     TEXT    NOT NULL,
    icon            TEXT    NOT NULL,   -- emoji
    badge_color     TEXT    NOT NULL,   -- hex color
    condition_type  TEXT    NOT NULL,
                    -- STREAK_WIN | STREAK_LOSE | CREDIT_REACH
                    -- CREDIT_ZERO | ALL_IN_WIN | HIGH_ODDS_WIN | TOP_RANK_FINAL
    condition_value INTEGER,
    is_active       INTEGER NOT NULL DEFAULT 1
);
```

### user_achievements
```sql
CREATE TABLE user_achievements (
    id             INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER  NOT NULL REFERENCES users(id),
    achievement_id INTEGER  NOT NULL REFERENCES achievements(id),
    earned_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    match_id       INTEGER  REFERENCES matches(id),
    UNIQUE(user_id, achievement_id)
);
```

### tournament_predictions
```sql
CREATE TABLE tournament_predictions (
    id              INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER  NOT NULL REFERENCES users(id),
    prediction_type TEXT     NOT NULL,  -- CHAMPION | RUNNER_UP | TOP_SCORER
    team_id         INTEGER  REFERENCES teams(id),    -- cho CHAMPION, RUNNER_UP
    player_name     TEXT,                             -- cho TOP_SCORER
    credit_bet      INTEGER  NOT NULL CHECK(credit_bet > 0),
    credit_result   INTEGER,
    status          TEXT     NOT NULL DEFAULT 'PENDING',  -- PENDING | WON | LOST | VOID
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, prediction_type)
);
```

### activity_feed
```sql
CREATE TABLE activity_feed (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    activity_type TEXT     NOT NULL,
                  -- PREDICTION | RANK_UP | ACHIEVEMENT | TOPUP | WIN | LOSE
    message       TEXT     NOT NULL,  -- "A vừa đoán trận Argentina vs Brazil"
    reference_id  INTEGER,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### hall_of_fame
```sql
CREATE TABLE hall_of_fame (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER  NOT NULL REFERENCES users(id),
    season        TEXT     NOT NULL,  -- "World Cup 2026"
    final_rank    INTEGER  NOT NULL,
    final_credits INTEGER  NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### system_config
```sql
CREATE TABLE system_config (
    key         TEXT     PRIMARY KEY,
    value       TEXT     NOT NULL,
    description TEXT     NOT NULL,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## Cơ chế Credit

```
User đặt 100 credit vào "Argentina Win" | odds 1.8

[Khi đặt cược]
  users.credits = users.credits - 100
  INSERT credit_transactions (amount = -100, type = BET)

[Sau trận - Argentina THẮNG]
  credit_result = ROUND(100 * 1.8) = 180
  users.credits = users.credits + 180
  INSERT credit_transactions (amount = +180, type = WIN)
  Lãi ròng: +80 credit

[Sau trận - Argentina THUA]
  credit_result = -100 (đã trừ khi đặt, không trừ thêm)
  INSERT credit_transactions (amount = 0, type = LOSE, description = "Thua kèo...")
  Mất ròng: 100 credit (đã bị trừ từ trước)
```

---

## 10 Huy hiệu mặc định

| Code | Tên | Icon | Điều kiện | Màu |
|---|---|---|---|---|
| `PROPHET` | Thánh Tiên Tri | 🔮 | Đoán đúng 10 trận liên tiếp | #9B59B6 |
| `SCORE_SNIPER` | Bắn Tỉa Tỷ Số | 🎯 | Đoán đúng tỷ số chính xác 5 lần | #E74C3C |
| `BAD_LUCK_KING` | Thánh Nhọ | 💀 | Sai 10 trận liên tiếp | #2C3E50 |
| `CONTRARIAN` | Ngược Chiều Vũ Trụ | 🙃 | Đoán ngược >80% người, thắng 5 lần | #E67E22 |
| `HIGH_ROLLER` | Tất Tay | 🎲 | Đặt toàn bộ credit vào 1 kèo và thắng | #F39C12 |
| `BROKE` | Người Ăn Mày | 😭 | Credit về 0 lần đầu tiên | #95A5A6 |
| `PHOENIX` | Phượng Hoàng Tái Sinh | 🦅 | Từ dưới 100 credit comeback lên top 3 | #E74C3C |
| `TYCOON` | Đại Gia | 💰 | Tích lũy đủ 10,000 credit | #F1C40F |
| `LUCKY_STAR` | Ngôi Sao May Mắn | ⭐ | Thắng 3 kèo có odds trên 5.0 | #1ABC9C |
| `CHAMPION` | Nhà Vô Địch | 🏆 | Đứng #1 BXH khi giải kết thúc | #FFD700 |

---

## Flyway Migration Plan

```
resources/db/migration/
├── V1__create_users.sql
├── V2__create_teams.sql
├── V3__create_matches.sql
├── V4__create_betting_lines.sql
├── V5__create_predictions.sql
├── V6__create_credit_transactions.sql
├── V7__create_achievements.sql
├── V8__create_user_achievements.sql
├── V9__create_tournament_predictions.sql
├── V10__create_activity_feed.sql
├── V11__create_hall_of_fame.sql
├── V12__create_system_config.sql
├── V13__create_indexes.sql
├── V14__seed_achievements.sql
└── V15__seed_system_config.sql
```

---

## Indexes

```sql
-- V13__create_indexes.sql
CREATE INDEX idx_matches_date        ON matches(match_date);
CREATE INDEX idx_matches_status      ON matches(status);
CREATE INDEX idx_predictions_user    ON predictions(user_id);
CREATE INDEX idx_predictions_match   ON predictions(match_id);
CREATE INDEX idx_credit_tx_user      ON credit_transactions(user_id);
CREATE INDEX idx_credit_tx_created   ON credit_transactions(created_at);
CREATE INDEX idx_betting_lines_match ON betting_lines(match_id);
CREATE INDEX idx_activity_feed_user  ON activity_feed(user_id);
CREATE INDEX idx_activity_feed_created ON activity_feed(created_at);
```

---

## Seed Data mặc định

### V15__seed_system_config.sql
```sql
INSERT INTO system_config (key, value, description) VALUES
  ('initial_credits',            '1000', 'Credit ban đầu khi tạo tài khoản'),
  ('lock_minutes_before_match',  '15',   'Khoá kèo trước bao nhiêu phút'),
  ('tournament_pred_lock_stage', 'QF',   'Khoá dự đoán toàn giải trước vòng nào'),
  ('min_bet',                    '10',   'Đặt cược tối thiểu'),
  ('max_bet_percent',            '100',  'Tối đa đặt bao nhiêu % credit hiện có (100 = all-in được)'),
  ('topup_amount',               '500',  'Số credit admin nạp thêm mỗi lần');
```
