# UI/UX Design - World Cup Prediction League

## Design System

### Style: Dark Sportsbook (Retro-Futurism adapted)
Lấy cảm hứng từ giao diện sportsbook chuyên nghiệp, tối, hiện đại.
Phù hợp: gaming, entertainment, competitive sports.

---

### Color Palette

| Token | Hex | Dùng cho |
|---|---|---|
| `--bg-base` | `#020617` | Nền chính (body) |
| `--bg-card` | `#0E1223` | Card, panel |
| `--bg-muted` | `#1A1E2F` | Sidebar, input background |
| `--bg-elevated` | `#1E2639` | Hover card, dropdown |
| `--text-primary` | `#F8FAFC` | Nội dung chính |
| `--text-secondary` | `#94A3B8` | Nhãn phụ, placeholder |
| `--text-muted` | `#64748B` | Thông tin ít quan trọng |
| `--accent-green` | `#22C55E` | Thắng, tích cực, CTA chính |
| `--accent-amber` | `#F59E0B` | Odds button, highlight |
| `--accent-blue` | `#3B82F6` | Liên kết, active tab |
| `--accent-red` | `#EF4444` | Thua, lỗi, khoá kèo |
| `--accent-purple` | `#8B5CF6` | Huy hiệu đặc biệt |
| `--border` | `#334155` | Viền card, divider |
| `--border-glow` | `rgba(34,197,94,0.3)` | Viền glow khi active |

### Typography

```css
/* Google Fonts */
@import url('https://fonts.googleapis.com/css2?family=Russo+One&family=Chakra+Petch:wght@300;400;500;600;700&display=swap');

/* Headings: Russo One - bold, competitive, sports feel */
--font-heading: 'Russo One', sans-serif;

/* Body: Chakra Petch - technical, readable, esports */
--font-body: 'Chakra Petch', sans-serif;
```

| Role | Font | Size | Weight |
|---|---|---|---|
| Page title | Russo One | 28–32px | 400 (inherently bold) |
| Section heading | Russo One | 20–24px | 400 |
| Card title | Chakra Petch | 16–18px | 600 |
| Body text | Chakra Petch | 14–16px | 400 |
| Label / badge | Chakra Petch | 12–13px | 500 |
| Credit number | Russo One | 24–36px | 400 |

### Spacing Scale (4px base)
`4 / 8 / 12 / 16 / 24 / 32 / 48 / 64px`

### Breakpoints
| Name | Width | Layout |
|---|---|---|
| Mobile | 375px | Single column, bottom nav |
| Tablet | 768px | 2 columns |
| Desktop | 1024px+ | Sidebar + main content |
| Wide | 1440px | Max-width container |

---

## Screen Flow

```
[Đăng nhập]
     │
     ▼
[Trang chủ] ────────────────────────────────────────────────┐
     │                                                       │
     ├──> [Lịch thi đấu]                                    │
     │         │                                            │
     │         └──> [Chi tiết trận + Đặt kèo]              │
     │                    │                                 │
     │                    └──> [Xác nhận đặt cược]         │
     │                              │                       │
     │                              └──> [Trang chủ] ──────┘
     │
     ├──> [Bảng xếp hạng]
     │         │
     │         └──> [Hồ sơ người chơi khác] (xem, không sửa)
     │
     ├──> [Lịch sử dự đoán]
     │         │
     │         └──> [Chi tiết trận đã đặt]
     │
     ├──> [Hồ sơ cá nhân]
     │         ├──> [Upload / Chọn avatar]
     │         └──> [Đổi mật khẩu]
     │
     ├──> [Hall of Fame]
     │
     └──> [Admin Panel] (chỉ ADMIN)
               ├──> [Dashboard]
               ├──> [Quản lý người dùng]
               │         └──> [Chi tiết user / Nạp credit / Reset pass]
               ├──> [Quản lý trận đấu]
               │         ├──> [Đồng bộ lịch thi đấu]
               │         ├──> [Upload file kèo]
               │         └──> [Nhập kết quả / Chấm điểm]
               ├──> [Quản lý huy hiệu]
               └──> [Cài đặt hệ thống]
```

---

## Navigation Structure

### Mobile (Bottom Navigation — 5 items)
```
┌─────────────────────────────────────────────────┐
│  🏠        ⚽        🏆        📋        👤     │
│ Trang chủ  Trận đấu  BXH    Lịch sử   Hồ sơ   │
└─────────────────────────────────────────────────┘
```

### Desktop (Top Navigation)
```
┌──────────────────────────────────────────────────────────────────┐
│  ⚽ WCPL        Trận đấu   BXH   Lịch sử   Hall of Fame         │
│                                              💰 1,250 xu  [👤▼] │
└──────────────────────────────────────────────────────────────────┘
```

- Credit balance luôn hiển thị trên navbar (như số dư ví)
- Avatar dropdown: Hồ sơ / Đổi mật khẩu / Đăng xuất
- Admin thấy thêm link "Admin" trong dropdown

---

## Wireframes chi tiết

---

### 1. Trang Đăng nhập

```
┌─────────────────────────────────────────────────┐
│                                                 │
│              ⚽  WORLD CUP 2026                 │
│           PREDICTION LEAGUE                     │
│         (Giải Đấu Dự Đoán Bóng Đá)             │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │  👤 Tên đăng nhập                       │   │
│  └─────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────┐   │
│  │  🔒 Mật khẩu                        👁  │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │         ĐĂNG NHẬP  →                    │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  Quên mật khẩu? Liên hệ admin                  │
│                                                 │
│  ─────────── World Cup 2026 ───────────         │
│  🇦🇷  🇧🇷  🏴󠁧󠁢󠁥󠁮󠁧󠁿  🇫🇷  🇩🇪  🇵🇹  🇪🇸  🇦🇺       │
│                                                 │
└─────────────────────────────────────────────────┘
```
- Background: Hình ảnh sân vận động tối, overlay gradient
- Hiệu ứng: Các cờ quốc gia chạy marquee nhẹ ở dưới
- Rate limit: Sau 3 lần sai → hiện CAPTCHA đơn giản; 10 lần → khoá IP 15 phút

---

### 2. Trang chủ (Homepage)

```
DESKTOP:
┌─────────────────────────────────────────────────────────────────┐
│ NAVBAR: Logo | Trận đấu | BXH | Lịch sử        💰 1,250 | 👤  │
├──────────────────────────┬──────────────────────────────────────┤
│                          │  FEED HOẠT ĐỘNG                     │
│  TOP 3 BXH               │  ─────────────────────────────────  │
│  ┌────────────────────┐  │  🔮 Nam vừa đạt huy hiệu Tiên Tri  │
│  │ 🥇 Nam   4,500 xu  │  │  ⭐ Minh vừa vượt lên Top 1        │
│  │ 🥈 Minh  3,200 xu  │  │  ⚽ Hùng vừa đặt kèo Argentina     │
│  │ 🥉 Hùng  2,800 xu  │  │  😭 Long vừa nhận huy hiệu Nhọ     │
│  └────────────────────┘  │  🔮 Lan vừa đặt kèo Brazil         │
│  [Xem BXH đầy đủ →]      │  ...                                │
│                          │                                     │
│  TRẬN HÔM NAY            │                                     │
│  ┌────────────────────┐  │  DỰ ĐOÁN TOÀN GIẢI                 │
│  │ ARG  vs  BRA  21:00│  │  ┌─────────────────────────────┐   │
│  │ [Xem kèo & Đặt cược│  │  │ Bạn chưa đặt dự đoán        │   │
│  └────────────────────┘  │  │ vô địch! Còn 5 ngày          │   │
│  ┌────────────────────┐  │  │ [Đặt ngay →]                 │   │
│  │ FRA  vs  ENG  00:00│  │  └─────────────────────────────┘   │
│  │ [Xem kèo & Đặt cược│  │                                     │
│  └────────────────────┘  │                                     │
│  [Xem tất cả trận →]     │                                     │
├──────────────────────────┴──────────────────────────────────────┤
│                         FOOTER                                  │
└─────────────────────────────────────────────────────────────────┘

MOBILE:
┌─────────────────────┐
│ ⚽ WCPL    💰1,250  │
├─────────────────────┤
│  TIN HOẠT ĐỘNG      │
│  ─────────────────  │
│  🔮 Nam đạt huy hiệu│
│  ⭐ Minh lên Top 1  │
│  ⚽ Hùng đặt kèo    │
│  ─────────────────  │
│  TOP BXH            │
│  🥇 Nam   4,500     │
│  🥈 Minh  3,200     │
│  🥉 Hùng  2,800     │
│  [Xem đầy đủ →]     │
│  ─────────────────  │
│  TRẬN HÔM NAY       │
│  [ARG vs BRA 21:00] │
│  [FRA vs ENG 00:00] │
│  [Xem thêm →]       │
├─────────────────────┤
│  🏠   ⚽   🏆  📋  👤│
└─────────────────────┘
```

---

### 3. Trang Lịch thi đấu

```
┌─────────────────────────────────────────────────────────────────┐
│  LỊCH THI ĐẤU WORLD CUP 2026                                   │
│                                                                 │
│  [Tất cả] [Vòng bảng] [Vòng 1/8] [Tứ kết] [Bán kết] [Chung kết]│
│  [Hôm nay] [Ngày mai] [Tuần này]                               │
│                                                                 │
│  ── Thứ Bảy, 14/06/2026 ──────────────────────────────────     │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  🇦🇷 ARGENTINA    21:00    🇧🇷 BRAZIL     🟢 Vòng bảng │   │
│  │  ─────────────────────────────────────────────────────  │   │
│  │  [ARG Thắng 1.8] [Hoà 3.2] [BRA Thắng 4.5]            │   │
│  │  [Over 2.5 ─ 2.1]  [Under 2.5 ─ 1.7]                 │   │
│  │                          🔓 Đóng kèo lúc 20:45         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  🇫🇷 PHÁP        00:00    🏴󠁧󠁢󠁥󠁮󠁧󠁿 ANH       🟢 Vòng bảng │   │
│  │  ─────────────────────────────────────────────────────  │   │
│  │  [FRA Thắng 2.1] [Hoà 3.0] [ENG Thắng 3.5]            │   │
│  │  [BTTS Có ─ 1.9]  [BTTS Không ─ 2.4]                  │   │
│  │                          🔓 Đóng kèo lúc 23:45         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ── Chủ Nhật, 15/06/2026 ──────────────────────────────────    │
│  ...                                                           │
└─────────────────────────────────────────────────────────────────┘
```

**Match Card States:**
- `SCHEDULED` + kèo chưa khoá → Odds buttons màu amber, có thể bấm
- `SCHEDULED` + kèo đã khoá → Odds buttons xám, icon 🔒
- `LIVE` → Badge đỏ nhấp nháy "TRỰC TIẾP", không thể đặt
- `FINISHED` → Hiển thị tỷ số cuối, badge xanh/đỏ cho các kèo

---

### 4. Trang Chi tiết trận + Panel Đặt kèo

```
┌─────────────────────────────────────────────────────────────────┐
│  ← Quay lại              Argentina vs Brazil                   │
│                          Vòng bảng - Bảng C                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│         🇦🇷                   21:00                  🇧🇷        │
│       ARGENTINA           14/06/2026                BRAZIL      │
│                                                                 │
│           ┌────────────────────────────────┐                   │
│           │  🔓 Đóng kèo sau: 02:45:30     │                   │
│           └────────────────────────────────┘                   │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  CHỌN KÈO ĐỂ ĐẶT CƯỢC                                         │
│                                                                 │
│  KẾT QUẢ TRẬN ĐẤU                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │ ARG Thắng    │  │    Hoà       │  │ BRA Thắng    │        │
│  │   x 1.8      │  │   x 3.2      │  │   x 4.5      │        │
│  │  [ĐẶT CƯỢC]  │  │  [ĐẶT CƯỢC]  │  │  [ĐẶT CƯỢC]  │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
│                                                                 │
│  TÀI/XỈU                                                       │
│  ┌─────────────────────────┐  ┌─────────────────────────┐    │
│  │  Over 2.5 Bàn  x 2.1   │  │  Under 2.5 Bàn  x 1.7  │    │
│  │       [ĐẶT CƯỢC]        │  │       [ĐẶT CƯỢC]        │    │
│  └─────────────────────────┘  └─────────────────────────┘    │
│                                                                 │
│  CẢ HAI ĐỘI GHI BÀN                                           │
│  ┌─────────────────────────┐  ┌─────────────────────────┐    │
│  │  Có ghi bàn  x 1.9     │  │  Không ghi bàn  x 2.4  │    │
│  │     [ĐẶT CƯỢC]          │  │     [ĐẶT CƯỢC]          │    │
│  └─────────────────────────┘  └─────────────────────────┘    │
│                                                                 │
│  KÈO ĐẶC BIỆT                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │  Messi ghi bàn đầu tiên   x 5.0    [ĐẶT CƯỢC]         │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  [Xem dự đoán của mọi người sau khi trận bắt đầu]  🔒 BÍ MẬT  │
└─────────────────────────────────────────────────────────────────┘
```

**Modal Đặt cược** (xuất hiện khi bấm nút "ĐẶT CƯỢC"):
```
┌─────────────────────────────────────┐
│  ĐẶT CƯỢC                        ✕ │
├─────────────────────────────────────┤
│  Kèo:   Argentina Thắng             │
│  Tỷ lệ: x 1.8                       │
│  ─────────────────────────────────  │
│  Số credit đặt:                     │
│  ┌───────────────────────────────┐  │
│  │  [ 200                    ]   │  │
│  └───────────────────────────────┘  │
│  [10] [50] [100] [500] [Tất tay]    │
│                                     │
│  Số dư: 1,250 xu                    │
│  ─────────────────────────────────  │
│  Nếu thắng:  +160 xu (nhận 360 xu) │
│  Nếu thua:   -200 xu               │
│  Số dư sau: 1,050 xu (nếu thua)    │
│                                     │
│  ┌─────────────────────────────┐   │
│  │     XÁC NHẬN ĐẶT CƯỢC      │   │
│  └─────────────────────────────┘   │
│  [Huỷ]                              │
└─────────────────────────────────────┘
```

---

### 5. Trang Bảng xếp hạng

```
┌─────────────────────────────────────────────────────────────────┐
│  BẢNG XẾP HẠNG                                                 │
│                                                                 │
│  [Toàn giải] [Vòng bảng] [Vòng loại TT] [Tuần này] [Hôm nay] │
├─────────────────────────────────────────────────────────────────┤
│  #   NGƯỜI CHƠI        CREDIT    THẮNG   THUA   STREAK        │
│  ─────────────────────────────────────────────────────────────  │
│  🥇 Nam          [avatar]  4,500 xu   25     7     🔥 5 liên   │
│  🥈 Minh         [avatar]  3,200 xu   20     10    ─            │
│  🥉 Hùng         [avatar]  2,800 xu   18     9     🔥 3 liên   │
│  4  Long         [avatar]  2,100 xu   15     12    ─            │
│  5  Lan          [avatar]  1,900 xu   14     11    ─            │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  │
│  7  Bạn (Tôi)    [avatar]  1,250 xu   10     12  💀 2 liên thua│
│                                                                 │
│  (Vị trí của bạn được highlight, sticky khi scroll)            │
└─────────────────────────────────────────────────────────────────┘
```

---

### 6. Trang Lịch sử dự đoán

```
┌─────────────────────────────────────────────────────────────────┐
│  LỊCH SỬ DỰ ĐOÁN CỦA TÔI                                      │
│                                                                 │
│  [Tất cả] [Đang chờ] [Thắng] [Thua]          Tổng: 22 cược   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ── 14/06/2026 ─────────────────────────────────────────────   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ✅ THẮNG   Argentina vs Brazil                          │   │
│  │ Kèo: Argentina Thắng (x1.8)  |  Đặt: 200 xu           │   │
│  │ Kết quả: 2-1  →  Nhận: +360 xu  (Lãi: +160 xu)        │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ❌ THUA   Argentina vs Brazil                           │   │
│  │ Kèo: Over 2.5 Bàn (x2.1)  |  Đặt: 100 xu             │   │
│  │ Kết quả: 2-1 (Không đủ 3 bàn)  →  Mất: -100 xu       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ── 13/06/2026 ─────────────────────────────────────────────   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⏳ CHỜ KẾT QUẢ   Pháp vs Anh                           │   │
│  │ Kèo: Pháp Thắng (x2.1)  |  Đặt: 150 xu               │   │
│  │ Trận đấu: 15/06/2026 00:00  →  Đang chờ...            │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 7. Trang Hồ sơ cá nhân

```
┌─────────────────────────────────────────────────────────────────┐
│  HỒ SƠ CÁ NHÂN                          [Chỉnh sửa]           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │      [AVATAR]     Nam Nguyễn                             │  │
│  │      [Đổi ảnh]    @nam_nguyen                            │  │
│  │                   Tham gia: 01/05/2026                   │  │
│  │                                                          │  │
│  │   💰 4,500 xu     🎯 25 thắng    ❌ 7 thua   🔥 5 liên  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  THÀNH TÍCH (3/10 huy hiệu)                                    │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  │
│  │  🔮    │  │  🎯    │  │  🏆    │  │  🌑    │  │  🌑    │  │
│  │Tiên Tri│  │Bắn Tỉa│  │VĐ 2026 │  │(Khoá) │  │(Khoá) │  │
│  └────────┘  └────────┘  └────────┘  └────────┘  └────────┘  │
│  (Bấm vào huy hiệu khoá → tooltip hiện điều kiện)             │
│                                                                 │
│  DỰ ĐOÁN TOÀN GIẢI                                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  🏆 Vô địch:  🇦🇷 Argentina   (Đặt 500 xu)              │  │
│  │  🥈 Á quân:   🇧🇷 Brazil      (Đặt 300 xu)              │  │
│  │  ⚽ Vua phá lưới: Messi        (Đặt 200 xu)              │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  BIỂU ĐỒ CREDIT                                               │
│  4500 ┤                               ╭──                     │
│  3000 ┤              ╭────╮     ╭─────╯                       │
│  1500 ┤      ╭───────╯    ╰─────╯                             │
│  1000 ┤──────╯                                                │
│       └─────────────────────────────────── ngày               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 8. Admin Dashboard

```
┌─────────────────────────────────────────────────────────────────┐
│  ⚙️ ADMIN PANEL                                  [← Về trang chủ]│
├───────────────────┬─────────────────────────────────────────────┤
│                   │                                             │
│  MENU             │  TỔNG QUAN HỆ THỐNG                       │
│  ─────────────    │  ┌────────┐ ┌────────┐ ┌────────┐         │
│  📊 Dashboard     │  │👥 10   │ │⚽ 48   │ │💰 32,500│         │
│  👥 Người dùng   │  │Người   │ │Trận    │ │Tổng xu │         │
│  ⚽ Trận đấu     │  │dùng    │ │đấu     │ │lưu hành│         │
│  🏷️ Huy hiệu    │  └────────┘ └────────┘ └────────┘         │
│  ⚙️ Cài đặt      │                                             │
│                   │  TRẬN CẦN XỬ LÝ                           │
│                   │  ┌─────────────────────────────────────┐  │
│                   │  │ ⚠️ ARG vs BRA - Chưa có kèo         │  │
│                   │  │ ⚠️ FRA vs ENG - Chưa có kết quả     │  │
│                   │  └─────────────────────────────────────┘  │
│                   │                                             │
│                   │  NGƯỜI DÙNG GẦN ĐÂY                       │
│                   │  ┌──────────────────────────────────────┐ │
│                   │  │ Nam  | 4,500xu | ✅ Hoạt động        │ │
│                   │  │ Minh | 3,200xu | ✅ Hoạt động        │ │
│                   │  │ Long | 100xu   | ⚠️ Gần hết credit   │ │
│                   │  └──────────────────────────────────────┘ │
│                   │  [Quản lý tất cả →]                       │
│                   │                                             │
└───────────────────┴─────────────────────────────────────────────┘
```

---

## Component Design

### Odds Button (Nút kèo)

```css
/* 3 trạng thái */

/* Normal */
.odds-btn {
  background: #1A1E2F;
  border: 1px solid #334155;
  color: #F8FAFC;
  padding: 10px 16px;
  border-radius: 8px;
  min-width: 110px;
  min-height: 52px;  /* touch target ≥ 44px */
  cursor: pointer;
  transition: all 150ms ease-out;
}

/* Selected */
.odds-btn.selected {
  background: rgba(34, 197, 94, 0.15);
  border-color: #22C55E;
  color: #22C55E;
  box-shadow: 0 0 12px rgba(34, 197, 94, 0.3);
}

/* Locked */
.odds-btn.locked {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #EF4444;
}
```

### Match Card

```css
.match-card {
  background: #0E1223;
  border: 1px solid #334155;
  border-radius: 12px;
  padding: 16px;
  transition: border-color 150ms ease-out, box-shadow 150ms ease-out;
}
.match-card:hover {
  border-color: rgba(34, 197, 94, 0.5);
  box-shadow: 0 4px 24px rgba(0,0,0,0.4);
}
```

### Credit Badge (Số dư)

```
┌───────────────────┐
│  💰  1,250 xu     │   ← Russo One, màu --accent-amber
└───────────────────┘
  Nền: rgba(245,158,11,0.1)
  Viền: rgba(245,158,11,0.3)
  Nhấp để xem lịch sử credit
```

### Leaderboard Row

```
┌─────────────────────────────────────────────────────┐
│  🥇  [avatar 32px]  Nam Nguyễn      4,500 xu  🔥5  │
│   1                  @nam                           │
└─────────────────────────────────────────────────────┘
  Highlight màu: rank 1 = gold, rank 2 = silver, rank 3 = bronze
  "Bạn" row: border-left: 3px solid #3B82F6 (xanh)
```

### Achievement Badge

```
┌──────────┐
│    🔮    │  Icon to, căn giữa
│ Tiên Tri │  Tên, font nhỏ
│ ──────── │
│ Đạt được │  Ngày đạt
│ 15/06    │
└──────────┘
Nền gradient theo badge_color
Border-radius: 12px
Khoá: grayscale filter + icon 🔒
```

---

## Responsive Behavior

### Credit Balance
- Mobile: Top bar → Hiện số ngắn gọn "1,250 xu"
- Desktop: Full widget trong navbar

### Match Cards
- Mobile: Stack dọc, odds buttons wrap 2 columns
- Desktop: Odds buttons hàng ngang

### Leaderboard
- Mobile: Rút gọn cột (ẩn Thắng/Thua, giữ Credit + Streak)
- Desktop: Full table

### Admin Panel
- Mobile: Sidebar thu gọn thành icon
- Desktop: Sidebar cố định bên trái

---

## Micro-interactions

| Trigger | Animation | Duration |
|---|---|---|
| Bấm Odds button | Scale 0.95 → 1.0 + border glow | 150ms |
| Đặt cược thành công | Credit badge flash xanh + số nhảy | 300ms |
| Nhận huy hiệu | Badge xuất hiện từ dưới lên + glow pulse | 400ms |
| Credit giảm | Số đỏ nhấp nháy 1 lần | 200ms |
| Kèo bị khoá | Card slide-in overlay "ĐÃ KHOÁ 🔒" | 200ms |
| Rank thay đổi | Row highlight + số vị trí animate | 300ms |

---

## Secret Mode

Khi trận chưa bắt đầu và Secret Mode bật:
- Trang lịch sử của người khác: Hiện "?? xu" thay vì số thật
- Activity feed: Không hiện tên kèo người khác đặt ("Hùng vừa đặt kèo trận Argentina")
- Sau khi match bắt đầu (status = LIVE): Tất cả dự đoán được hiện

---

## Accessibility

- Contrast ratio: tất cả text ≥ 4.5:1
- Touch target: tất cả button ≥ 44×44px
- Keyboard navigation: Tab order logic
- Form labels: Visible label cho mọi input
- Loading states: Skeleton screen thay vì blank
- Error messages: Hiện ngay dưới field liên quan
- Reduced motion: Tắt animation khi `prefers-reduced-motion: reduce`
