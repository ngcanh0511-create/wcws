/**
 * Cấu hình runtime — được load trước tất cả các script khác.
 * Khi deploy bằng container, file này được ghi lại từ biến API_BASE_URL.
 *
 * Ví dụ production:
 *   window.APP_CONFIG = { apiBase: 'https://api.example.com' }
 */
window.APP_CONFIG = {
  apiBase: 'http://localhost:8080',
};
