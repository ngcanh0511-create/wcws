if (!AuthGuard.requireAdmin()) throw new Error('Not admin');
Navbar.render('admin');

const CONFIG_LABELS = {
  initial_credits: 'Credit ban đầu khi tạo tài khoản',
  lock_minutes_before_match: 'Khóa kèo trước trận (phút)',
  tournament_pred_lock_stage: 'Khóa dự đoán giải từ vòng',
  min_bet: 'Đặt cược tối thiểu (xu)',
  max_bet_percent: 'Đặt cược tối đa (% credit hiện có)',
  topup_amount: 'Xu nạp thêm khi cháy túi',
};

async function load() {
  try {
    const configs = await Api.get('/api/v1/admin/configs');
    renderConfigs(configs);
  } catch (err) {
    document.getElementById('config-list').innerHTML =
      `<div class="alert alert-danger m-3">${err.message}</div>`;
  }
}

function renderConfigs(configs) {
  if (configs.length === 0) {
    document.getElementById('config-list').innerHTML =
      '<div class="text-center py-4 text-muted">Chưa có config nào.</div>';
    return;
  }

  const html = configs.map(c => `
    <div class="p-3 border-bottom d-flex align-items-center gap-3 flex-wrap"
         style="border-color:var(--wcpl-border)!important">
      <div class="flex-1">
        <div class="fw-semibold small">${CONFIG_LABELS[c.key] || c.key}</div>
        <code class="text-muted" style="font-size:0.75rem">${c.key}</code>
        ${c.description ? `<div class="text-muted" style="font-size:0.78rem">${c.description}</div>` : ''}
      </div>
      <div class="d-flex align-items-center gap-2">
        <input type="text" id="cfg-${c.key}" class="form-control form-control-sm"
               value="${c.value}" style="width:130px">
        <button class="btn btn-sm btn-outline-success" onclick="saveConfig('${c.key}')">Lưu</button>
      </div>
    </div>`).join('');

  document.getElementById('config-list').innerHTML = html;
}

async function saveConfig(key) {
  const value = document.getElementById(`cfg-${key}`)?.value?.trim();
  if (value === undefined) return;

  try {
    await Api.put(`/api/v1/admin/configs/${key}?value=${encodeURIComponent(value)}`);
    Toast.success('Đã lưu cấu hình!');
  } catch (err) { Toast.error(err.message); }
}

load();
