// Nếu đã đăng nhập → redirect thẳng về dashboard
if (Storage.isLoggedIn()) {
  window.location.href = '/pages/dashboard.html';
}

const form = document.getElementById('login-form');
const errorMsg = document.getElementById('error-msg');
const loginBtn = document.getElementById('login-btn');

// Toggle hiện/ẩn mật khẩu
document.getElementById('toggle-pw').addEventListener('click', () => {
  const pw = document.getElementById('password');
  pw.type = pw.type === 'password' ? 'text' : 'password';
});

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  errorMsg.classList.add('d-none');

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;

  if (!username || !password) {
    showError('Vui lòng nhập đầy đủ thông tin.');
    return;
  }

  setLoading(true);
  try {
    await Auth.login(username, password);
    window.location.href = '/pages/dashboard.html';
  } catch (err) {
    showError(err.message || 'Đăng nhập thất bại. Vui lòng thử lại.');
  } finally {
    setLoading(false);
  }
});

function showError(msg) {
  errorMsg.textContent = msg;
  errorMsg.classList.remove('d-none');
}

function setLoading(loading) {
  loginBtn.disabled = loading;
  loginBtn.textContent = loading ? 'Đang đăng nhập...' : 'Đăng nhập';
}
