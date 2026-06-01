if (!AuthGuard.require()) throw new Error('Not authenticated');
Navbar.render('tournament');

const TYPES = [
  { type: 'CHAMPION',    icon: 'bi-trophy', label: 'Đội vô địch',    desc: 'Chọn đội sẽ vô địch World Cup 2026' },
  { type: 'RUNNER_UP',   icon: 'bi-award', label: 'Đội á quân',      desc: 'Chọn đội về nhì' },
  { type: 'TOP_SCORER',  icon: 'bi-bullseye', label: 'Vua phá lưới',    desc: 'Nhập tên cầu thủ ghi bàn nhiều nhất' },
];

let myPredictions = {};
let teams = [];

async function init() {
  try {
    const [mine, teamsData] = await Promise.all([
      Api.get('/api/v1/tournament-predictions/me'),
      Api.get('/api/v1/teams'),
    ]);
    mine.forEach(p => { myPredictions[p.predictionType] = p; });
    teams = teamsData;
    render();
  } catch (err) {
    document.getElementById('prediction-cards').innerHTML =
      `<div class="alert alert-danger">${err.message}</div>`;
  }
}

function render() {
  const html = TYPES.map(t => {
    const existing = myPredictions[t.type];

    let content;
    if (existing) {
      const selectedName = existing.team?.name || existing.teamName || existing.playerName || '—';
      content = `
        <div class="alert alert-success py-2 mb-0 small">
          <i class="bi bi-check2-circle me-1"></i>Đã đặt: <strong>${selectedName}</strong>
          - ${Format.credits(existing.creditBet)} 
          ${existing.status !== 'PENDING'
            ? `<br>${Format.predictionStatusBadge(existing.status)}`
            : ''}
        </div>`;
    } else if (t.type === 'TOP_SCORER') {
      content = `
        <div>
          <div class="mb-2">
            <label class="form-label small">Tên cầu thủ</label>
            <input type="text" id="input-${t.type}" class="form-control"
                   placeholder="Ví dụ: Mbappé">
          </div>
          <div class="mb-3">
            <label class="form-label small">Số xu cược</label>
            <input type="number" id="bet-${t.type}" class="form-control" min="10" placeholder="Nhập số xu...">
          </div>
          <button class="btn btn-wcpl-primary w-100" onclick="place('${t.type}')">Xác nhận</button>
        </div>`;
    } else {
      const options = teams.map(team =>
        `<option value="${team.id}">${team.name} (${team.shortName})</option>`
      ).join('');
      content = `
        <div>
          <div class="mb-2">
            <label class="form-label small">Chọn đội</label>
            <select id="input-${t.type}" class="form-select">
              <option value="">-- Chọn đội --</option>
              ${options}
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label small">Số xu cược</label>
            <input type="number" id="bet-${t.type}" class="form-control" min="10" placeholder="Nhập số xu...">
          </div>
          <button class="btn btn-wcpl-primary w-100" onclick="place('${t.type}')">Xác nhận</button>
        </div>`;
    }

    return `
      <div class="wcpl-card p-4 mb-3">
        <div class="fw-bold mb-1"><i class="bi ${t.icon} me-2"></i>${t.label}</div>
        <div class="text-muted small mb-3">${t.desc}</div>
        ${content}
      </div>`;
  }).join('');

  document.getElementById('prediction-cards').innerHTML = html;
}

async function place(type) {
  const betInput = document.getElementById(`bet-${type}`);
  const valueInput = document.getElementById(`input-${type}`);
  const creditBet = parseInt(betInput?.value, 10);

  if (!creditBet || creditBet < 10) {
    Toast.warning('Số xu tối thiểu là 10.');
    return;
  }

  const body = { predictionType: type, creditBet };
  if (type === 'TOP_SCORER') {
    const name = valueInput?.value?.trim();
    if (!name) { Toast.warning('Nhập tên cầu thủ.'); return; }
    body.playerName = name;
  } else {
    const teamId = valueInput?.value;
    if (!teamId) { Toast.warning('Vui lòng chọn đội.'); return; }
    body.teamId = parseInt(teamId, 10);
  }

  try {
    const result = await Api.post('/api/v1/tournament-predictions', body);
    myPredictions[type] = result;
    Toast.success('Đặt dự đoán thành công!');
    render();
  } catch (err) {
    Toast.error(err.message);
  }
}

init();
