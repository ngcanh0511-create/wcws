# API Design - World Cup Prediction League

## Convention

- **Base URL:** `https://api.yourdomain.com/api/v1`
- **Auth header:** `Authorization: Bearer <accessToken>`
- **Format:** JSON
- **Error format:** `{ "code": "ERROR_CODE", "message": "Mô tả lỗi" }`

---

## Auth

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| POST | `/auth/login` | Public | Đăng nhập |
| POST | `/auth/refresh` | Public | Lấy access token mới |
| POST | `/auth/logout` | User | Huỷ refresh token |
| POST | `/auth/change-password` | User | Đổi mật khẩu |

### POST /auth/login
```json
Request:
{
  "username": "john",
  "password": "abc123"
}

Response 200:
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "user": {
    "id": 1,
    "displayName": "John",
    "avatarUrl": "/avatars/john.jpg",
    "credits": 1000,
    "role": "USER"
  }
}
```

### POST /auth/refresh
```json
Request:  { "refreshToken": "eyJ..." }
Response: { "accessToken": "eyJ..." }
```

### POST /auth/change-password
```json
Request: { "currentPassword": "old", "newPassword": "new" }
Response: { "message": "Đổi mật khẩu thành công" }
```

---

## User

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/users/me` | User | Thông tin cá nhân + credit |
| PUT | `/users/me` | User | Cập nhật display name |
| POST | `/users/me/avatar` | User | Upload avatar (multipart/form-data) |
| PUT | `/users/me/avatar/default` | User | Chọn avatar mặc định |
| GET | `/users/me/predictions` | User | Lịch sử dự đoán |
| GET | `/users/me/achievements` | User | Huy hiệu đã đạt |
| GET | `/users/me/credits/history` | User | Lịch sử tăng/giảm credit |

### GET /users/me
```json
Response 200:
{
  "id": 1,
  "username": "john",
  "displayName": "John Doe",
  "avatarUrl": "/avatars/john.jpg",
  "credits": 850,
  "role": "USER",
  "createdAt": "2026-05-01T10:00:00Z"
}
```

### GET /users/me/predictions?page=0&size=20
```json
Response 200:
{
  "data": [
    {
      "id": 101,
      "match": {
        "id": 1,
        "homeTeam": "Argentina",
        "awayTeam": "Brazil",
        "matchDate": "2026-06-14T18:00:00Z",
        "score": "2-1"
      },
      "bettingLine": { "description": "Argentina Win", "odds": 1.8 },
      "creditBet": 200,
      "creditResult": 360,
      "status": "WON"
    }
  ],
  "total": 15,
  "page": 0,
  "size": 20
}
```

---

## Matches

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/matches` | Public | Danh sách trận |
| GET | `/matches/{id}` | Public | Chi tiết trận |
| GET | `/matches/{id}/betting-lines` | User | Kèo của trận + trạng thái user đã đặt |

### GET /matches
Query params: `stage`, `status`, `date`, `page`, `size`

```json
Response 200:
{
  "data": [
    {
      "id": 1,
      "homeTeam": {
        "id": 1, "name": "Argentina", "shortName": "ARG", "flagUrl": "/flags/arg.png"
      },
      "awayTeam": {
        "id": 2, "name": "Brazil", "shortName": "BRA", "flagUrl": "/flags/bra.png"
      },
      "matchDate": "2026-06-14T18:00:00Z",
      "stage": "GROUP",
      "status": "SCHEDULED",
      "homeScore": null,
      "awayScore": null,
      "predictionLockedAt": "2026-06-14T17:45:00Z"
    }
  ],
  "total": 48
}
```

### GET /matches/{id}/betting-lines
```json
Response 200:
{
  "matchId": 1,
  "isLocked": false,
  "lines": [
    {
      "id": 5,
      "lineType": "WIN_HOME",
      "description": "Argentina Win",
      "odds": 1.8,
      "myPrediction": {           // null nếu user chưa đặt
        "creditBet": 200,
        "status": "PENDING"
      }
    },
    {
      "id": 6,
      "lineType": "DRAW",
      "description": "Draw",
      "odds": 3.2,
      "myPrediction": null
    }
  ]
}
```

---

## Predictions

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| POST | `/predictions` | User | Đặt kèo |
| GET | `/predictions/{id}` | User | Chi tiết 1 dự đoán |
| GET | `/predictions/match/{matchId}` | User | Tất cả kèo user đã đặt cho 1 trận |

### POST /predictions
```json
Request:
{
  "bettingLineId": 5,
  "creditBet": 200
}

Response 201:
{
  "id": 101,
  "bettingLine": { "id": 5, "description": "Argentina Win", "odds": 1.8 },
  "creditBet": 200,
  "status": "PENDING",
  "creditsRemaining": 800
}
```

---

## Tournament Predictions

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/tournament-predictions/options` | Public | Danh sách lựa chọn (đội, tên cầu thủ) |
| POST | `/tournament-predictions` | User | Đặt dự đoán toàn giải |
| GET | `/tournament-predictions/me` | User | Dự đoán toàn giải của user |

### POST /tournament-predictions
```json
Request:
{
  "predictionType": "CHAMPION",   // CHAMPION | RUNNER_UP | TOP_SCORER
  "teamId": 1,                    // cho CHAMPION, RUNNER_UP
  "playerName": null,             // cho TOP_SCORER
  "creditBet": 500
}
```

---

## Leaderboard

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/leaderboard` | Public | BXH |
| GET | `/leaderboard/me` | User | Vị trí của user hiện tại |

### GET /leaderboard?period=overall&limit=10
`period`: `daily` | `weekly` | `group_stage` | `knockout` | `overall`

```json
Response 200:
{
  "period": "overall",
  "data": [
    {
      "rank": 1,
      "user": { "id": 3, "displayName": "Nam", "avatarUrl": "..." },
      "credits": 4500,
      "correctPredictions": 25,
      "totalPredictions": 32
    }
  ],
  "myRank": {
    "rank": 5,
    "credits": 1800
  }
}
```

---

## Activity Feed

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/feed` | User | Feed hoạt động (phân trang) |

### GET /feed?page=0&size=20
```json
Response 200:
{
  "data": [
    {
      "id": 501,
      "user": { "displayName": "Nam", "avatarUrl": "..." },
      "activityType": "ACHIEVEMENT",
      "message": "Nam vừa đạt huy hiệu 🔮 Thánh Tiên Tri",
      "createdAt": "2026-06-15T20:30:00Z"
    },
    {
      "id": 500,
      "user": { "displayName": "Minh", "avatarUrl": "..." },
      "activityType": "RANK_UP",
      "message": "Minh vừa vượt lên Top 1 BXH",
      "createdAt": "2026-06-15T20:25:00Z"
    }
  ]
}
```

---

## Hall of Fame

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/hall-of-fame` | Public | BXH cuối mùa các giải |

---

## Admin

| Method | Endpoint | Auth | Mô tả |
|---|---|---|---|
| GET | `/admin/users` | Admin | Danh sách user |
| POST | `/admin/users` | Admin | Tạo tài khoản mới |
| PUT | `/admin/users/{id}/lock` | Admin | Khoá tài khoản |
| PUT | `/admin/users/{id}/unlock` | Admin | Mở khoá |
| POST | `/admin/users/{id}/topup` | Admin | Nạp thêm credit |
| PUT | `/admin/users/{id}/reset-password` | Admin | Reset mật khẩu |
| POST | `/admin/matches/sync` | Admin | Đồng bộ lịch từ football-data.org |
| PUT | `/admin/matches/{id}/result` | Admin | Cập nhật kết quả trận |
| POST | `/admin/matches/{id}/betting-lines/upload` | Admin | Upload file kèo (.txt) |
| POST | `/admin/matches/{id}/score` | Admin | Trigger chấm điểm |
| GET | `/admin/achievements` | Admin | Danh sách huy hiệu |
| POST | `/admin/achievements` | Admin | Thêm huy hiệu |
| PUT | `/admin/achievements/{id}` | Admin | Sửa huy hiệu |
| GET | `/admin/config` | Admin | Xem system config |
| PUT | `/admin/config` | Admin | Cập nhật system config |
| GET | `/admin/stats` | Admin | Thống kê hệ thống |
| POST | `/admin/hall-of-fame/snapshot` | Admin | Lưu BXH cuối mùa |

### POST /admin/users (Tạo tài khoản invite-only)
```json
Request:
{
  "username": "john",
  "displayName": "John Doe",
  "password": "temp123"
}
Response 201:
{
  "id": 10,
  "username": "john",
  "displayName": "John Doe",
  "credits": 1000
}
```

### PUT /admin/matches/{id}/result
```json
Request:
{
  "homeScore": 2,
  "awayScore": 1
}
```

### POST /admin/users/{id}/topup
```json
Request:  { "amount": 500, "reason": "Giải thưởng tuần" }
Response: { "userId": 5, "creditsAdded": 500, "newBalance": 1300 }
```

---

## Format file kèo

Admin upload file `.txt` theo format:

```
MATCH=1001

Argentina Win|1.8
Draw|3.2
Brazil Win|4.5

Over 2.5 Goals|2.1
Under 2.5 Goals|1.7

Both Teams Score|1.9
No Goal|2.4

Messi First Goal|5.0
```

Parser mapping:
- `<Tên đội> Win` → `WIN_HOME` hoặc `WIN_AWAY`
- `Draw` → `DRAW`
- `Over X Goals` → `OVER`
- `Under X Goals` → `UNDER`
- `Both Teams Score` → `BTTS`
- `No Goal` → `BTTS` (result = false)
- `<Cầu thủ> First Goal` → `FIRST_SCORER`
- Còn lại → `SPECIAL`

---

## Error Codes

| Code | HTTP Status | Mô tả |
|---|---|---|
| `UNAUTHORIZED` | 401 | Chưa đăng nhập hoặc token hết hạn |
| `FORBIDDEN` | 403 | Không đủ quyền |
| `USER_NOT_FOUND` | 404 | Không tìm thấy user |
| `MATCH_NOT_FOUND` | 404 | Không tìm thấy trận đấu |
| `BETTING_LINE_NOT_FOUND` | 404 | Không tìm thấy kèo |
| `PREDICTION_LOCKED` | 400 | Kèo đã bị khoá, không thể đặt |
| `INSUFFICIENT_CREDITS` | 400 | Không đủ credit để đặt |
| `ALREADY_PREDICTED` | 400 | Đã dự đoán loại này rồi (áp dụng cho dự đoán toàn giải) |
| `ACCOUNT_LOCKED` | 403 | Tài khoản bị khoá |
| `RATE_LIMIT_EXCEEDED` | 429 | Quá nhiều request, thử lại sau |
| `VALIDATION_ERROR` | 400 | Dữ liệu đầu vào không hợp lệ |
| `INVALID_FILE_FORMAT` | 400 | File kèo sai định dạng |
