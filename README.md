# World Cup Prediction League

Website dự đoán World Cup cho nhóm bạn, dùng credit nội bộ. Dự án gồm:

- Backend: Java 21, Spring Boot 3, Gradle, SQLite, Flyway
- Frontend: HTML, CSS, Vanilla JavaScript, Bootstrap 5

## Chạy Project Local

Mở PowerShell tại thư mục root:

```powershell
cd E:\code\CLAUDE_AI_TEST1
```

### 1. Chạy Backend

```powershell
cd E:\code\CLAUDE_AI_TEST1\backend
.\gradlew.bat bootRun
```

Backend chạy tại:

```text
http://localhost:8080
```

Kiểm tra API public:

```powershell
Invoke-WebRequest -Uri http://localhost:8080/api/v1/matches -UseBasicParsing
```

Database SQLite mặc định nằm tại:

```text
%USERPROFILE%\wcpl\wcpl.db
```

### 2. Chạy Frontend Bằng Live Server

Mở PowerShell khác tại root project:

```powershell
cd E:\code\CLAUDE_AI_TEST1
npx.cmd --yes live-server frontend --port=5500 --host=127.0.0.1 --no-browser
```

Frontend chạy tại:

```text
http://127.0.0.1:5500
```

Trang login:

```text
http://127.0.0.1:5500/pages/login.html
```

Nếu PowerShell báo lỗi `npm.ps1 cannot be loaded`, dùng `npx.cmd` như câu lệnh trên, không dùng `npx`.

## Tài Khoản Admin Mặc Định

Khi database mới được tạo, backend tự seed admin:

```text
username: admin
password: 123qwe!@#
```

Sau khi login, vào:

```text
http://127.0.0.1:5500/pages/admin/matches.html
```

## Đồng Bộ Lịch Thi Đấu

Ở trang Admin Matches, bấm:

```text
Đồng bộ lịch
```

Frontend sẽ gọi:

```http
POST /api/v1/admin/matches/sync
```

Provider mặc định là API miễn phí không cần key:

```text
https://worldcup26.ir/get
```

Hiện provider này trả:

- 48 đội
- 72 trận vòng bảng
- 12 bảng đấu

## Nhập Kèo Trận Đấu

Ở trang Admin Matches:

1. Bấm nút `Kèo` ở trận cần nhập.
2. Dán nội dung kèo vào ô textarea.
3. Bấm `Lưu kèo`.

Ví dụ nội dung:

```text
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

Frontend gọi endpoint:

```http
POST /api/v1/admin/matches/{matchId}/betting-lines/text
```

Body:

```json
{
  "content": "MATCH=1001\n\nArgentina Win|1.8\nDraw|3.2"
}
```

Endpoint upload file cũ vẫn còn:

```http
POST /api/v1/admin/matches/{matchId}/betting-lines/upload
```

## Cấu Hình API Bóng Đá

Mặc định dùng `worldcup26`:

```yaml
app:
  football-api:
    provider: worldcup26
    worldcup26:
      base-url: https://worldcup26.ir/get
```

Có thể đổi sang API-FOOTBALL nếu có API key:

```powershell
$env:FOOTBALL_API_PROVIDER="api-football"
$env:FOOTBALL_API_KEY="your_api_key"
cd E:\code\CLAUDE_AI_TEST1\backend
.\gradlew.bat bootRun
```

## Test Backend

```powershell
cd E:\code\CLAUDE_AI_TEST1\backend
.\gradlew.bat test
```

## URL Quan Trọng

```text
Frontend Login:         http://127.0.0.1:5500/pages/login.html
Frontend Dashboard:     http://127.0.0.1:5500/pages/dashboard.html
Admin Matches:          http://127.0.0.1:5500/pages/admin/matches.html
Backend API:            http://localhost:8080/api/v1
Matches API:            http://localhost:8080/api/v1/matches
```
