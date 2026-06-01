/**
 * Quản lý localStorage — tập trung key names để tránh typo.
 */
const Storage = {
  KEYS: {
    ACCESS_TOKEN: 'wcpl_access_token',
    REFRESH_TOKEN: 'wcpl_refresh_token',
    USER: 'wcpl_user',
  },

  getAccessToken() {
    return localStorage.getItem(this.KEYS.ACCESS_TOKEN);
  },

  getRefreshToken() {
    return localStorage.getItem(this.KEYS.REFRESH_TOKEN);
  },

  getUser() {
    const raw = localStorage.getItem(this.KEYS.USER);
    return raw ? JSON.parse(raw) : null;
  },

  saveAuth(accessToken, refreshToken, user) {
    localStorage.setItem(this.KEYS.ACCESS_TOKEN, accessToken);
    localStorage.setItem(this.KEYS.REFRESH_TOKEN, refreshToken);
    localStorage.setItem(this.KEYS.USER, JSON.stringify(user));
  },

  saveAccessToken(token) {
    localStorage.setItem(this.KEYS.ACCESS_TOKEN, token);
  },

  updateUser(user) {
    localStorage.setItem(this.KEYS.USER, JSON.stringify(user));
  },

  clear() {
    Object.values(this.KEYS).forEach(k => localStorage.removeItem(k));
  },

  isLoggedIn() {
    return !!this.getAccessToken();
  },
};
