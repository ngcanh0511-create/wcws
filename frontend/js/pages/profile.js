if (!AuthGuard.require()) throw new Error('Not authenticated');
Navbar.render('profile');

let currentTab = 'predictions';
let currentPage = 0;
const PAGE_SIZE = 10;

// ── Load profile ───────────────────────────────────────────────────────

async function init() {
  try {
    const [profile, stats] = await Promise.all([
      Api.get('/api/v1/users/me'),
      Api.get('/api/v1/users/me/stats'),
    ]);
    renderProfile(profile);
    renderStats(stats);
    loadTabData();
  } catch (err) {
    Toast.error('Không tải được hồ sơ: ' + err.message);
  }
}

function renderProfile(p) {
  const defaultAvatar = '/assets/avatars/defaults/default_1.png';
  const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
  const avatarSrc = p.avatarUrl
    ? `${apiBase}${p.avatarUrl}`
    : defaultAvatar;

  document.getElementById('avatar-img').src = avatarSrc;
  document.getElementById('display-name-text').textContent = p.displayName;
  document.getElementById('username-text').textContent = `@${p.username}`;
  document.getElementById('credit-display').textContent = Format.credits(p.credits);
  document.getElementById('display-name-input').value = p.displayName;

  Storage.updateUser({ ...Storage.getUser(), credits: p.credits, avatarUrl: p.avatarUrl });
}

function renderStats(s) {
  const total = s.totalBets || 0;
  const wins = s.wins || 0;
  const rate = total > 0 ? ((wins / total) * 100).toFixed(1) + '%' : '—';
  const profit = s.totalProfit !== undefined ? s.totalProfit : 0;

  document.getElementById('stat-total').textContent = total;
  document.getElementById('stat-wins').textContent = wins;
  document.getElementById('stat-rate').textContent = rate;
  document.getElementById('stat-profit').textContent = (profit >= 0 ? '+' : '') + Format.credits(profit);
  document.getElementById('stat-profit').className = `fs-4 fw-bold ${profit >= 0 ? 'text-success' : 'text-danger'}`;
}

// ── Avatar Upload ──────────────────────────────────────────────────────

document.getElementById('avatar-upload').addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  if (file.size > 5 * 1024 * 1024) {
    Toast.error('File quá lớn. Tối đa 5MB.');
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  try {
    const result = await Api.upload('/api/v1/users/me/avatar', formData);
    const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
    const src = `${apiBase}${result.avatarUrl}?t=${Date.now()}`;
    document.getElementById('avatar-img').src = src;
    Storage.updateUser({ ...Storage.getUser(), avatarUrl: result.avatarUrl });
    Toast.success('Cập nhật avatar thành công!');
  } catch (err) {
    Toast.error('Upload thất bại: ' + err.message);
  }
  e.target.value = '';
});

// ── Default Avatars ────────────────────────────────────────────────────

const DEFAULT_AVATARS = [
  // Cầu thủ nổi tiếng
  { file: 'defaults/messi.png',       label: 'Messi' },
  { file: 'defaults/ronaldo.png',     label: 'Ronaldo' },
  { file: 'defaults/mbappe.png',      label: 'Mbappé' },
  { file: 'defaults/neymar.png',      label: 'Neymar' },
  { file: 'defaults/haaland.png',     label: 'Haaland' },
  { file: 'defaults/modric.png',      label: 'Modric' },
  { file: 'defaults/salah.png',       label: 'Salah' },
  { file: 'defaults/son.png',         label: 'Son' },
  { file: 'defaults/kane.png',        label: 'Kane' },
  { file: 'defaults/vinicius.png',    label: 'Vinícius' },
  { file: 'defaults/benzema.png',     label: 'Benzema' },
  { file: 'defaults/lewandowski.png', label: 'Lewandowski' },
  // Avatar mặc định
  { file: 'defaults/default_1.png', label: 'Mặc định 1' },
  { file: 'defaults/default_2.png', label: 'Mặc định 2' },
  { file: 'defaults/default_3.png', label: 'Mặc định 3' },
  { file: 'defaults/default_4.png', label: 'Mặc định 4' },
  { file: 'defaults/default_5.png', label: 'Mặc định 5' },
  { file: 'defaults/default_6.png', label: 'Mặc định 6' },
  { file: 'defaults/default_7.png', label: 'Mặc định 7' },
  { file: 'defaults/default_8.png', label: 'Mặc định 8' },
];

function showDefaultAvatars() {
  document.getElementById('default-avatars').innerHTML = DEFAULT_AVATARS.map(av => `
    <div class="col-3 text-center" style="cursor:pointer" onclick="setDefaultAvatar('${av.file}')">
      <img src="/assets/avatars/${av.file}"
           class="rounded-circle border border-secondary"
           style="width:60px;height:60px;object-fit:cover;transition:transform 0.15s"
           onmouseover="this.style.transform='scale(1.12)'; this.style.borderColor='#198754'"
           onmouseout="this.style.transform=''; this.style.borderColor=''"
           onerror="this.parentElement.style.display='none'" alt="${av.label}">
      <div style="font-size:0.7rem; margin-top:0.3rem; color:#8a9bb5">${av.label}</div>
    </div>`).join('');
  new bootstrap.Modal(document.getElementById('avatar-modal')).show();
}

async function setDefaultAvatar(filePath) {
  // filePath ví dụ: "defaults/messi.png" — controller nhận @RequestParam name
  const filename = filePath.split('/').pop();
  try {
    const result = await Api.put(`/api/v1/users/me/avatar/default?name=${encodeURIComponent(filename)}`);
    const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
    document.getElementById('avatar-img').src =
      `${apiBase}${result.avatarUrl}?t=${Date.now()}`;
    Storage.updateUser({ ...Storage.getUser(), avatarUrl: result.avatarUrl });
    bootstrap.Modal.getInstance(document.getElementById('avatar-modal'))?.hide();
    Toast.success('Đã đổi avatar!');
  } catch (err) {
    Toast.error(err.message);
  }
}

// ── Update Profile ─────────────────────────────────────────────────────

async function updateProfile() {
  const displayName = document.getElementById('display-name-input').value.trim();
  if (!displayName) { Toast.warning('Tên không được để trống.'); return; }

  try {
    const result = await Api.put('/api/v1/users/me', { displayName });
    document.getElementById('display-name-text').textContent = result.displayName;
    Storage.updateUser({ ...Storage.getUser(), displayName: result.displayName });
    Toast.success('Cập nhật thành công!');
  } catch (err) {
    Toast.error(err.message);
  }
}

// ── Change Password ────────────────────────────────────────────────────

async function changePassword() {
  const currentPassword = document.getElementById('current-pw').value;
  const newPassword = document.getElementById('new-pw').value;

  if (!currentPassword || !newPassword) {
    Toast.warning('Vui lòng nhập đầy đủ.');
    return;
  }
  if (newPassword.length < 6) {
    Toast.warning('Mật khẩu mới ít nhất 6 ký tự.');
    return;
  }

  try {
    await Auth.changePassword(currentPassword, newPassword);
    document.getElementById('current-pw').value = '';
    document.getElementById('new-pw').value = '';
    Toast.success('Đổi mật khẩu thành công!');
  } catch (err) {
    Toast.error(err.message);
  }
}

// ── Tabs ───────────────────────────────────────────────────────────────

function switchTab(tab) {
  currentTab = tab;
  currentPage = 0;
  document.getElementById('tab-predictions').classList.toggle('active', tab === 'predictions');
  document.getElementById('tab-credits').classList.toggle('active', tab === 'credits');
  loadTabData();
}

async function loadTabData() {
  const container = document.getElementById('tab-content');
  container.innerHTML = '<div class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm me-2"></div>Đang tải...</div>';

  try {
    if (currentTab === 'predictions') await loadPredictions();
    else await loadCredits();
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Lỗi: ${err.message}</div>`;
  }
}

async function loadPredictions() {
  const data = await Api.get(`/api/v1/users/me/predictions?page=${currentPage}&size=${PAGE_SIZE}`);
  const items = data.content || [];
  const total = data.totalPages || 0;

  if (items.length === 0) {
    document.getElementById('tab-content').innerHTML =
      '<div class="text-center py-4 text-muted">Chưa có dự đoán nào.</div>';
  } else {
    document.getElementById('tab-content').innerHTML = items.map(p => `
      <div class="prediction-row">
        <div class="d-flex justify-content-between align-items-start gap-2">
          <div class="flex-1">
            <div class="small fw-semibold">${p.matchName || '—'}</div>
            <div class="small text-muted">${p.bettingLineDescription || ''}</div>
            <div class="small text-muted">${DateUtils.relative(p.createdAt)}</div>
          </div>
          <div class="text-end">
            ${Format.predictionStatusBadge(p.status)}
            <div class="small text-muted mt-1">Đặt: ${Format.credits(p.creditBet)}</div>
            ${p.creditResult !== null
              ? `<div class="${p.creditResult >= 0 ? 'credit-change-positive' : 'credit-change-negative'}">
                   ${p.creditResult >= 0 ? '+' : ''}${Format.credits(p.creditResult)}
                 </div>`
              : ''}
          </div>
        </div>
      </div>`).join('');
  }

  renderPagination(total);
}

async function loadCredits() {
  const data = await Api.get(`/api/v1/users/me/credits/history?page=${currentPage}&size=${PAGE_SIZE}`);
  const items = data.content || [];
  const total = data.totalPages || 0;

  if (items.length === 0) {
    document.getElementById('tab-content').innerHTML =
      '<div class="text-center py-4 text-muted">Chưa có giao dịch nào.</div>';
  } else {
    document.getElementById('tab-content').innerHTML = items.map(tx => `
      <div class="prediction-row">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <div class="small fw-semibold">${tx.description || tx.type}</div>
            <div class="small text-muted">${DateUtils.relative(tx.createdAt)}</div>
          </div>
          <div class="text-end">
            <div class="${tx.amount >= 0 ? 'credit-change-positive' : 'credit-change-negative'} fw-bold">
              ${tx.amount >= 0 ? '+' : ''}${Format.credits(tx.amount)}
            </div>
            <div class="small text-muted">Còn lại: ${Format.credits(tx.balanceAfter)}</div>
          </div>
        </div>
      </div>`).join('');
  }

  renderPagination(total);
}

function renderPagination(totalPages) {
  const row = document.getElementById('pagination-row');
  if (totalPages <= 1) { row.innerHTML = ''; return; }

  let html = '';
  if (currentPage > 0) html += `<button class="btn btn-sm btn-outline-secondary" onclick="goPage(${currentPage - 1})">← Trước</button>`;
  html += `<span class="small text-muted align-self-center">Trang ${currentPage + 1}/${totalPages}</span>`;
  if (currentPage < totalPages - 1) html += `<button class="btn btn-sm btn-outline-secondary" onclick="goPage(${currentPage + 1})">Sau →</button>`;
  row.innerHTML = html;
}

function goPage(page) {
  currentPage = page;
  loadTabData();
}

// ── Init ───────────────────────────────────────────────────────────────
init();
