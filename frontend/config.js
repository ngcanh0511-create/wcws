/**
 * Cấu hình runtime — được load trước tất cả các script khác.
 * Khi deploy lên Cloudflare Pages, thay giá trị apiBase bằng URL thực.
 *
 * Ví dụ production:
 *   window.APP_CONFIG = { apiBase: 'https://wcpl-api.onrender.com' }
 */
window.APP_CONFIG = {
  apiBase: 'http://localhost:8080',
};
