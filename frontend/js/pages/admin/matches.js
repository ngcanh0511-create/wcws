if (!AuthGuard.requireAdmin()) throw new Error('Not admin');
Navbar.render('admin');

let matches = [];

async function load() {
  try {
    matches = await Api.get('/api/v1/matches');
    renderTable();
  } catch (err) {
    document.getElementById('match-table').innerHTML =
      `<div class="alert alert-danger m-3">${err.message}</div>`;
  }
}

async function syncMatches() {
  const btn = document.getElementById('sync-matches-btn');
  const originalText = btn?.textContent || 'Đồng bộ lịch';
  if (btn) {
    btn.disabled = true;
    btn.textContent = 'Đang đồng bộ...';
  }

  try {
    const result = await Api.post('/api/v1/admin/matches/sync');
    Toast.success(
      `Đã đồng bộ ${result.teamsUpserted || 0} đội, ${result.matchesUpserted || 0} trận.`
    );
    if (result.matchesSkipped) {
      Toast.warning(`${result.matchesSkipped} trận bị bỏ qua vì thiếu đội.`);
    }
    await load();
  } catch (err) {
    Toast.error(err.message);
  } finally {
    if (btn) {
      btn.disabled = false;
      btn.textContent = originalText;
    }
  }
}

function renderTable() {
  if (matches.length === 0) {
    document.getElementById('match-table').innerHTML =
      '<div class="text-center py-4 text-muted">Chưa có trận đấu nào.</div>';
    return;
  }

  const rows = matches.map(m => {
    const homeName = m.homeTeam?.name || '?';
    const awayName = m.awayTeam?.name || '?';
    const hasLines = m.bettingLines?.length > 0;

    return `
      <tr>
        <td>
          <div class="fw-semibold small">${homeName} vs ${awayName}</div>
          <div class="text-muted" style="font-size:0.78rem">${DateUtils.formatMatchTime(m.matchDate)} • ${Format.stageLabel(m.stage)}</div>
        </td>
        <td>${Format.matchStatusBadge(m.status)}</td>
        <td>
          ${m.status === 'FINISHED'
            ? `<span class="fw-bold">${Format.score(m.homeScore, m.awayScore)}</span>`
            : '<span class="text-muted small">—</span>'}
        </td>
        <td>
          <span class="badge ${hasLines ? 'bg-success' : 'bg-secondary'}">
            ${hasLines ? m.bettingLines.length + ' kèo' : 'Chưa có kèo'}
          </span>
        </td>
        <td>
          <div class="d-flex gap-1 flex-wrap">
            <button class="btn btn-xs btn-outline-primary" onclick="showBetting(${m.id})">
              <i class="bi bi-list-check me-1"></i>Kèo
            </button>
            <button class="btn btn-xs btn-outline-success" onclick="showResult(${m.id}, '${escAttr(homeName)}', '${escAttr(awayName)}')">
              <i class="bi bi-bar-chart me-1"></i>Kết quả
            </button>
            <button class="btn btn-xs btn-outline-warning" onclick="grade(${m.id})">
              <i class="bi bi-check2-circle me-1"></i>Chấm
            </button>
          </div>
        </td>
      </tr>`;
  }).join('');

  document.getElementById('match-table').innerHTML = `
    <div class="table-responsive">
      <table class="table table-dark table-hover mb-0" style="--bs-table-bg: transparent">
        <thead><tr>
          <th>Trận đấu</th><th>Trạng thái</th><th>Tỷ số</th><th>Kèo</th><th>Thao tác</th>
        </tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>`;
}

function escAttr(s) { return s.replace(/'/g, "\\'"); }

// ── Upload kèo ─────────────────────────────────────────────────────────

function showBetting(matchId) {
  const match = matches.find(m => String(m.id) === String(matchId));
  document.getElementById('betting-match-id').value = matchId;
  document.getElementById('betting-content').value = buildBettingContent(match);
  new bootstrap.Modal(document.getElementById('betting-modal')).show();
}

function buildBettingContent(match) {
  const matchId = match?.id || '';
  const lines = match?.bettingLines || [];

  if (lines.length === 0) return `MATCH=${matchId}\n\n`;

  const grouped = lines.reduce((acc, line) => {
    const key = line.lineType || 'SPECIAL';
    if (!acc[key]) acc[key] = [];
    acc[key].push(line);
    return acc;
  }, {});

  const body = Object.entries(grouped)
    .map(([lineType, groupLines]) => {
      const lineText = groupLines
        .map(line => `${line.description || ''}|${formatOddsForInput(line.odds)}|LINE=${line.id}`)
        .join('\n');
      return `# ${friendlyLineType(lineType)}\n${lineText}`;
    })
    .join('\n\n');

  return `MATCH=${matchId}\n\n${body}\n`;
}

function friendlyLineType(lineType) {
  const map = {
    WIN_HOME: 'Đội thắng',
    DRAW: 'Hòa',
    WIN_AWAY: 'Đội thắng',
    OVER: 'Tài',
    UNDER: 'Xỉu',
    BTTS: 'Cả hai đội ghi bàn',
    EXACT_SCORE: 'Tỷ số chính xác',
    FIRST_SCORER: 'Cầu thủ ghi bàn đầu',
    SPECIAL: 'Kèo đặc biệt',
  };
  return map[lineType] || lineType;
}

function formatOddsForInput(odds) {
  const value = Number(odds);
  if (!Number.isFinite(value)) return '';
  return String(Math.round(value * 1000) / 1000);
}

async function uploadBettingLines() {
  const matchId = document.getElementById('betting-match-id').value;
  const content = document.getElementById('betting-content').value.trim();
  if (!content) { Toast.warning('Nhập nội dung kèo.'); return; }

  try {
    await Api.post(`/api/v1/admin/matches/${matchId}/betting-lines/text`, { content });
    Toast.success('Đã lưu kèo thành công!');
    bootstrap.Modal.getInstance(document.getElementById('betting-modal'))?.hide();
    load();
  } catch (err) { Toast.error(err.message); }
}

// ── Kết quả ────────────────────────────────────────────────────────────

function showResult(matchId, home, away) {
  document.getElementById('result-match-id').value = matchId;
  document.getElementById('result-match-name').textContent = `${home} vs ${away}`;
  document.getElementById('home-score').value = 0;
  document.getElementById('away-score').value = 0;
  new bootstrap.Modal(document.getElementById('result-modal')).show();
}

async function setResult() {
  const matchId = document.getElementById('result-match-id').value;
  const home = parseInt(document.getElementById('home-score').value, 10);
  const away = parseInt(document.getElementById('away-score').value, 10);

  try {
    await Api.put(`/api/v1/admin/matches/${matchId}/result?homeScore=${home}&awayScore=${away}`);
    Toast.success('Đã lưu kết quả!');
    bootstrap.Modal.getInstance(document.getElementById('result-modal'))?.hide();
    load();
  } catch (err) { Toast.error(err.message); }
}

async function setResultAndGrade() {
  const matchId = document.getElementById('result-match-id').value;
  const home = parseInt(document.getElementById('home-score').value, 10);
  const away = parseInt(document.getElementById('away-score').value, 10);

  try {
    await Api.put(`/api/v1/admin/matches/${matchId}/result?homeScore=${home}&awayScore=${away}`);
    await grade(matchId, false);
    Toast.success('Đã lưu kết quả và chấm điểm!');
    bootstrap.Modal.getInstance(document.getElementById('result-modal'))?.hide();
    load();
  } catch (err) { Toast.error(err.message); }
}

async function grade(matchId, showSuccess = true) {
  try {
    const result = await Api.post(`/api/v1/admin/matches/${matchId}/grade`);
    if (showSuccess) Toast.success(result.message || 'Đã chấm điểm!');
  } catch (err) { Toast.error(err.message); }
}

// Thêm style cho nút nhỏ
const style = document.createElement('style');
style.textContent = '.btn-xs { padding: 0.2rem 0.5rem; font-size: 0.78rem; }';
document.head.appendChild(style);

load();
