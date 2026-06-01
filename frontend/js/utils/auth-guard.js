/**
 * Bảo vệ trang cần đăng nhập.
 * Gọi AuthGuard.require() ở đầu mỗi page JS.
 * Gọi AuthGuard.requireAdmin() cho trang admin.
 */
const AuthGuard = {
  require() {
    if (!Storage.isLoggedIn()) {
      window.location.href = '/pages/login.html';
      return false;
    }
    return true;
  },

  requireAdmin() {
    if (!this.require()) return false;
    if (!Auth.isAdmin()) {
      window.location.href = '/pages/dashboard.html';
      return false;
    }
    return true;
  },
};
