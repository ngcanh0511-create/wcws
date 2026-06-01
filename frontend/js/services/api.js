/**
 * HTTP client trung tâm.
 *
 * Tính năng:
 * - Tự động gắn Bearer token vào header
 * - Khi nhận 401, thử refresh token 1 lần rồi retry
 * - Nếu refresh thất bại → logout + redirect về login
 * - Throw lỗi dạng { status, code, message } để UI xử lý
 */

// Đọc API base từ config (inject lúc deploy) hoặc fallback về localhost
const API_BASE = window.APP_CONFIG?.apiBase || 'http://localhost:8080';

let _isRefreshing = false;
let _refreshQueue = []; // các request đang chờ token mới

async function _doRefresh() {
  const refreshToken = Storage.getRefreshToken();
  if (!refreshToken) throw new Error('No refresh token');

  const res = await fetch(`${API_BASE}/api/v1/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });

  if (!res.ok) throw new Error('Refresh failed');
  const data = await res.json();
  Storage.saveAccessToken(data.accessToken);
  return data.accessToken;
}

async function request(method, path, body, isRetry = false) {
  const headers = { 'Content-Type': 'application/json' };
  const token = Storage.getAccessToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const options = { method, headers };
  if (body !== undefined && body !== null) {
    options.body = JSON.stringify(body);
  }

  const res = await fetch(`${API_BASE}${path}`, options);

  // Token hết hạn — thử refresh 1 lần
  if (res.status === 401 && !isRetry) {
    if (_isRefreshing) {
      // Xếp hàng chờ token mới
      return new Promise((resolve, reject) => {
        _refreshQueue.push({ resolve, reject, method, path, body });
      });
    }

    _isRefreshing = true;
    try {
      await _doRefresh();
      _isRefreshing = false;

      // Giải phóng hàng đợi
      _refreshQueue.forEach(({ resolve, method, path, body }) =>
        resolve(request(method, path, body, true))
      );
      _refreshQueue = [];

      return request(method, path, body, true);
    } catch {
      _isRefreshing = false;
      _refreshQueue.forEach(({ reject }) => reject(new Error('Session expired')));
      _refreshQueue = [];
      Storage.clear();
      window.location.href = '/pages/login.html';
      throw new Error('Session expired');
    }
  }

  if (!res.ok) {
    let errBody = {};
    try { errBody = await res.json(); } catch {}
    const err = new Error(errBody.message || `HTTP ${res.status}`);
    err.status = res.status;
    err.code = errBody.code || 'UNKNOWN';
    throw err;
  }

  // 204 No Content
  if (res.status === 204) return null;

  return res.json();
}

const Api = {
  get: (path) => request('GET', path),
  post: (path, body) => request('POST', path, body),
  put: (path, body) => request('PUT', path, body),
  delete: (path) => request('DELETE', path),

  // Upload file (multipart) — không dùng JSON header
  async upload(path, formData) {
    const headers = {};
    const token = Storage.getAccessToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers,
      body: formData,
    });

    if (!res.ok) {
      let errBody = {};
      try { errBody = await res.json(); } catch {}
      const err = new Error(errBody.message || `Upload failed`);
      err.status = res.status;
      throw err;
    }
    return res.json();
  },
};
