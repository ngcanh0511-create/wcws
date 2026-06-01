if (!AuthGuard.requireAdmin()) throw new Error('Not admin');
Navbar.render('admin');

let users = [];

async function load() {
  try {
    users = await Api.get('/api/v1/admin/users');
    renderTable();
  } catch (err) {
    document.getElementById('user-table').innerHTML =
      `<div class="alert alert-danger m-3">${err.message}</div>`;
  }
}

function renderTable() {
  if (users.length === 0) {
    document.getElementById('user-table').innerHTML =
      '<div class="text-center py-4 text-muted">Chưa có user nào.</div>';
    return;
  }

  const rows = users.map(u => `
    <tr>
      <td>${u.username}</td>
      <td>${u.displayName}</td>
      <td class="text-warning">${Format.credits(u.credits)}</td>
      <td>
        <span class="badge ${u.role === 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">${u.role}</span>
      </td>
      <td>
        <span class="badge ${u.isLocked ? 'bg-danger' : 'bg-success'}">${u.isLocked ? 'Bị khóa' : 'Hoạt động'}</span>
      </td>
      <td>
        <div class="d-flex gap-1 flex-wrap">
          ${u.isLocked
            ? `<button class="btn btn-xs btn-outline-success" onclick="toggleLock(${u.id}, false)">Mở khóa</button>`
            : `<button class="btn btn-xs btn-outline-danger" onclick="toggleLock(${u.id}, true)">Khóa</button>`}
          <button class="btn btn-xs btn-outline-warning" onclick="showResetPw(${u.id})">Reset PW</button>
          <button class="btn btn-xs btn-outline-primary" onclick="showCredit(${u.id})">Credit</button>
        </div>
      </td>
    </tr>`).join('');

  document.getElementById('user-table').innerHTML = `
    <div class="table-responsive">
      <table class="table table-dark table-hover mb-0" style="--bs-table-bg: transparent">
        <thead><tr>
          <th>Username</th><th>Tên</th><th>Credit</th><th>Vai trò</th><th>Trạng thái</th><th>Thao tác</th>
        </tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>`;
}

// ── Actions ────────────────────────────────────────────────────────────

async function toggleLock(userId, lock) {
  try {
    await Api.post(`/api/v1/admin/users/${userId}/${lock ? 'lock' : 'unlock'}`);
    Toast.success(lock ? 'Đã khóa tài khoản' : 'Đã mở khóa tài khoản');
    load();
  } catch (err) { Toast.error(err.message); }
}

function showCreateModal() {
  ['new-username', 'new-displayname', 'new-password'].forEach(id => {
    document.getElementById(id).value = '';
  });
  new bootstrap.Modal(document.getElementById('create-modal')).show();
}

async function createUser() {
  const username = document.getElementById('new-username').value.trim();
  const displayName = document.getElementById('new-displayname').value.trim();
  const password = document.getElementById('new-password').value;

  if (!username || !displayName || !password) {
    Toast.warning('Vui lòng nhập đầy đủ thông tin.');
    return;
  }

  try {
    await Api.post('/api/v1/admin/users', { username, displayName, password });
    Toast.success('Tạo tài khoản thành công!');
    bootstrap.Modal.getInstance(document.getElementById('create-modal'))?.hide();
    load();
  } catch (err) { Toast.error(err.message); }
}

function showResetPw(userId) {
  document.getElementById('reset-user-id').value = userId;
  document.getElementById('reset-pw-value').value = '';
  new bootstrap.Modal(document.getElementById('reset-pw-modal')).show();
}

async function resetPassword() {
  const userId = document.getElementById('reset-user-id').value;
  const newPassword = document.getElementById('reset-pw-value').value;
  if (!newPassword || newPassword.length < 6) {
    Toast.warning('Mật khẩu ít nhất 6 ký tự.');
    return;
  }
  try {
    await Api.put(`/api/v1/admin/users/${userId}/password`, { newPassword });
    Toast.success('Đã reset mật khẩu!');
    bootstrap.Modal.getInstance(document.getElementById('reset-pw-modal'))?.hide();
  } catch (err) { Toast.error(err.message); }
}

function showCredit(userId) {
  document.getElementById('credit-user-id').value = userId;
  document.getElementById('credit-amount').value = '';
  document.getElementById('credit-reason').value = '';
  new bootstrap.Modal(document.getElementById('credit-modal')).show();
}

async function updateCredit() {
  const userId = document.getElementById('credit-user-id').value;
  const amount = parseInt(document.getElementById('credit-amount').value, 10);
  const reason = document.getElementById('credit-reason').value.trim();

  if (isNaN(amount) || amount === 0) { Toast.warning('Nhập số xu hợp lệ.'); return; }

  try {
    await Api.put(`/api/v1/admin/users/${userId}/credits`, { amount, reason: reason || 'Admin điều chỉnh' });
    Toast.success('Đã cập nhật credit!');
    bootstrap.Modal.getInstance(document.getElementById('credit-modal'))?.hide();
    load();
  } catch (err) { Toast.error(err.message); }
}

// Thêm style cho nút nhỏ
const style = document.createElement('style');
style.textContent = '.btn-xs { padding: 0.2rem 0.5rem; font-size: 0.78rem; }';
document.head.appendChild(style);

load();
