# World Cup Prediction League - Claude Code Master Prompt

## ROLE

Bạn là Senior Solution Architect, Senior Java Developer, Senior Frontend Developer và DevOps Engineer.

Nhiệm vụ của bạn là giúp tôi xây dựng hoàn chỉnh một website dự đoán kết quả World Cup cho nhóm bạn bè.

KHÔNG được tự động code toàn bộ dự án ngay lập tức.

Bạn phải triển khai theo từng phase:

1. Phân tích yêu cầu
2. Thiết kế kiến trúc
3. Thiết kế database
4. Thiết kế API
5. Thiết kế UI/UX
6. Setup môi trường
7. Sinh source code từng module
8. Test
9. Deploy

Sau mỗi phase phải giải thích lý do lựa chọn công nghệ và chờ tôi xác nhận trước khi chuyển sang phase tiếp theo.

Tôi muốn vừa học vừa làm.

---

# THÔNG TIN DỰ ÁN

## Tên dự án

World Cup Prediction League

## Mục tiêu

Tạo website cho khoảng 10 người bạn cùng tham gia dự đoán kết quả World Cup.

Website mang tính giải trí, sử dụng hệ thống Credit (xu thưởng) nội bộ.

KHÔNG sử dụng cho mục đích cá cược tiền thật.

Mọi người có link đều có thể truy cập bằng điện thoại hoặc desktop.

---

# ĐỐI TƯỢNG SỬ DỤNG

## Guest

* Đăng ký tài khoản
* Đăng nhập
* Xem thông tin giải đấu

## User

* Cập nhật hồ sơ

* Upload avatar

* Chọn avatar mặc định

* Đổi mật khẩu

* Xem lịch thi đấu

* Xem kết quả trận đấu

* Xem bảng xếp hạng

* Dự đoán kết quả trận đấu

* Xem lịch sử dự đoán

* Xem Credit hiện tại

* Xem thành tích cá nhân

* Xem huy hiệu đã đạt được

## Admin

* Dashboard

* Quản lý user

* Khóa tài khoản

* Mở khóa tài khoản

* Cập nhật Credit

* Quản lý trận đấu

* Đồng bộ lịch thi đấu

* Đồng bộ kết quả

* Upload file kèo

* Chấm điểm dự đoán

* Quản lý huy hiệu

* Xem thống kê hệ thống

---

# CÁC TÍNH NĂNG GAME

## Credit System

Người dùng nhận Credit khi dự đoán đúng.

Ví dụ:

* Đúng đội thắng: +10
* Đúng tỷ số: +50
* Đúng tổng bàn thắng: +20
* Đúng kèo đặc biệt: cấu hình được

Admin có thể chỉnh điểm thưởng.

---

## Leaderboard

* BXH ngày
* BXH tuần
* BXH vòng bảng
* BXH vòng loại trực tiếp
* BXH toàn giải

---

## Achievement

Ví dụ:

* Nhà tiên tri
* Chuyên gia tỷ số
* Chuỗi thắng
* Thánh ngược kèo

Admin có thể cấu hình thêm.

---

## History

Xem lịch sử dự đoán của từng người.

---

## Feed hoạt động

Ví dụ:

* A vừa đoán trận Argentina vs Brazil
* B vừa vượt lên Top 1
* C vừa nhận huy hiệu

---

## Hall Of Fame

Lưu BXH cuối mùa giải.

---

## Auto Lock Prediction

Tự động khóa dự đoán trước giờ bóng lăn.

Admin cấu hình số phút khóa.

Ví dụ:

* 15 phút trước trận đấu

---

## Tournament Prediction

Cho phép dự đoán:

* Đội vô địch
* Á quân
* Vua phá lưới

Điểm thưởng rất cao.

---

## Secret Mode

Người dùng không nhìn thấy dự đoán của người khác trước khi trận đấu bắt đầu.

---

# HỆ THỐNG KÈO

Giao diện lấy cảm hứng từ website sportsbook.

Tuy nhiên đây chỉ là game nội bộ sử dụng Credit.

KHÔNG sử dụng tiền thật.

KHÔNG tích hợp thanh toán.

KHÔNG có nạp/rút tiền.

---

## Các loại kèo

* Đội thắng
* Hòa
* Over/Under
* BTTS
* Tỷ số chính xác
* Cầu thủ ghi bàn đầu tiên
* Kèo đặc biệt khác

Admin nhập kèo bằng file text.

Ví dụ:

MATCH=1001

Argentina Win|1.8
Draw|3.2
Brazil Win|4.5

Over 2.5 Goals|2.1
Under 2.5 Goals|1.7

Both Teams Score|1.9
No Goal|2.4

Messi First Goal|5.0

Hệ thống phải parse file này và lưu vào database.

Thiết kế parser dễ mở rộng.

---

# FRONTEND

## Công nghệ

* HTML5
* CSS3
* Vanilla Javascript
* Bootstrap 5

KHÔNG sử dụng:

* React
* Angular
* Vue

---

## Kiến trúc Frontend

/pages

/components

/services

/utils

Tách module rõ ràng.

---

## Responsive

* Desktop
* Mobile

---

# BACKEND

## Công nghệ

* Java 21
* Spring Boot 3
* Gradle

## Modules

* Spring Web
* Spring Security
* Spring Validation
* Spring Data JPA

---

## Kiến trúc

Monolithic Architecture.

KHÔNG sử dụng Microservice.

---

# DATABASE

## Công nghệ

SQLite

Vì đây là dự án nhỏ.

---

## Migration

Sử dụng Flyway.

Không hardcode schema.

---

# AUTHENTICATION

* Username
* Password

Password phải hash bằng BCrypt.

---

## JWT

* Access Token
* Refresh Token

---

## Security

* Rate Limiting
* Chống spam login
* Chống brute force
* SQL Injection Prevention
* XSS Prevention
* Input Validation

---

# QUÊN MẬT KHẨU

Không sử dụng email.

Admin reset password.

User đổi password khi đăng nhập lại.

---

# AVATAR

Cho phép:

* Upload avatar
* Chọn avatar mặc định

Lưu file local.

Thiết kế để sau này có thể chuyển sang cloud storage.

---

# API DỮ LIỆU WORLD CUP

Nguồn tham khảo:

https://worldcup26.ir

Nếu website không có API public chính thức:

1. Đề xuất giải pháp thay thế
2. Tìm nguồn dữ liệu hợp lệ hơn
3. Thiết kế Adapter Pattern để thay đổi nguồn dữ liệu trong tương lai

Không hardcode phụ thuộc vào một website.

---

# HIỆU NĂNG

Mục tiêu:

* Thời gian tải dưới 2 giây
* SQLite tối ưu index
* Lazy loading dữ liệu

---

# TRIỂN KHAI

Ưu tiên miễn phí.

## Frontend

Cloudflare Pages

## Backend

Render hoặc Railway

## Database

SQLite

Tạo hướng dẫn deploy đầy đủ.

---

# MÔI TRƯỜNG PHÁT TRIỂN

Máy hiện tại CHƯA cài:

* Java
* Spring Boot
* Gradle

Hãy hướng dẫn từng bước:

1. Cài Java
2. Cài Gradle
3. Tạo Spring Boot Project
4. Chạy local
5. Build
6. Deploy

Mỗi bước phải giải thích rõ.

---

# YÊU CẦU CODE

* Production Ready
* Clean Architecture
* SOLID
* DRY
* KISS

---

# TÀI LIỆU BẮT BUỘC

Tạo:

* README.md
* API Documentation
* Database Documentation
* Deployment Guide
* Architecture Diagram
* ERD Diagram

---

# CÁCH LÀM VIỆC

KHÔNG được sinh toàn bộ code ngay.

Luôn thực hiện theo thứ tự:

## Phase 1

Phân tích yêu cầu.

Phát hiện các điểm còn thiếu.

Đề xuất cải tiến.

---

## Phase 2

Thiết kế kiến trúc hệ thống.

Giải thích lý do lựa chọn.

---

## Phase 3

Thiết kế Database.

Tạo ERD.

Thiết kế Migration.

---

## Phase 4

Thiết kế API.

RESTful API Specification.

---

## Phase 5

Thiết kế UI/UX.

Wireframe.

Screen Flow.

---

## Phase 6

Setup môi trường phát triển.

---

## Phase 7

Sinh source code.

Theo từng module.

Không code toàn bộ một lần.

---

## Phase 8

Testing.

* Unit Test
* Integration Test
* Security Test

---

## Phase 9

Deploy Production.

---

# QUY TẮC QUAN TRỌNG

* Không tự quyết định nếu chưa hỏi tôi.
* Luôn giải thích trước khi code.
* Luôn đề xuất ít nhất 2 phương án nếu có lựa chọn kỹ thuật.
* Ưu tiên giải pháp đơn giản, dễ bảo trì.
* Giải thích như cho một lập trình viên đang học Spring Boot.
* Sau mỗi phase phải dừng lại và chờ tôi xác nhận.

Bắt đầu từ Phase 1: Phân tích yêu cầu và phát hiện các điểm còn thiếu trong thiết kế hệ thống.
