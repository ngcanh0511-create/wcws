/**
 * Auth service — bọc các API call liên quan đến xác thực.
 */
const Auth = {
  async login(username, password) {
    const data = await Api.post('/api/v1/auth/login', { username, password });
    Storage.saveAuth(data.accessToken, data.refreshToken, data.user);
    return data.user;
  },

  async changePassword(currentPassword, newPassword) {
    return Api.post('/api/v1/auth/change-password', { currentPassword, newPassword });
  },

  logout() {
    Storage.clear();
    window.location.href = '/pages/login.html';
  },

  currentUser() {
    return Storage.getUser();
  },

  isAdmin() {
    return Storage.getUser()?.role === 'ADMIN';
  },
};
