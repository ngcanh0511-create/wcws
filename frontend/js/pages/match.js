if (!AuthGuard.require()) throw new Error('Not authenticated');

Navbar.render('dashboard');

const params = new URLSearchParams(window.location.search);
const matchId = params.get('id');

if (!matchId) window.location.href = '/pages/dashboard.html';

let matchData = null;
let selectedLine = null;
let myMatchPredictions = [];

const mainContent = document.getElementById('main-content');
const betSlipSection = document.getElementById('bet-slip-section');
const lockedNotice = document.getElementById('locked-notice');
const betAmountInput = document.getElementById('bet-amount');

// ── Load match data ────────────────────────────────────────────────────

async function init() {
  try {
    matchData = await Api.get(`/api/v1/matches/${matchId}`);
    renderHeader();
    renderBettingLines();
    await renderMyPredictions();
    mainContent.style.removeProperty('display');
  } catch (err) {
    document.getElementById('match-header').innerHTML =
      `<div class="alert alert-danger">Không tải được trận đấu: ${err.message}</div>`;
  }
}

// ── Match Header ───────────────────────────────────────────────────────

function renderHeader() {
  const m = matchData;
  const isLocked = m.predictionLocked;

  const homeFlag = m.homeTeam?.flagUrl
    ? `<img src="${m.homeTeam.flagUrl}" alt="${m.homeTeam.name}" class="team-flag" width="48" height="48">`
    : '<i class="bi bi-flag fs-2 text-muted"></i>';
  const awayFlag = m.awayTeam?.flagUrl
    ? `<img src="${m.awayTeam.flagUrl}" alt="${m.awayTeam.name}" class="team-flag" width="48" height="48">`
    : '<i class="bi bi-flag fs-2 text-muted"></i>';

  const scoreSection = m.status === 'FINISHED'
    ? `<div class="match-score" style="font-size:2rem">${Format.score(m.homeScore, m.awayScore)}</div>`
    : `<div class="text-center">
         <div class="text-muted fw-bold mb-1">${DateUtils.formatMatchTime(m.matchDate)}</div>
         <div>${Format.matchStatusBadge(m.status)}</div>
         ${isLocked ? '<div class="badge bg-warning text-dark mt-1"><i class="bi bi-lock me-1"></i>Đã đóng kèo</div>' : ''}
       </div>`;

  document.getElementById('match-header').innerHTML = `
    <div class="wcpl-card p-4 mb-4">
      <div class="text-center text-muted small mb-3">
        ${Format.stageLabel(m.stage)} • ${DateUtils.formatLong(m.matchDate)}
      </div>
      <div class="d-flex align-items-center justify-content-between gap-3">
        <div class="text-center flex-1">
          <div class="mb-2">${homeFlag}</div>
          <div class="fw-bold">${m.homeTeam?.name || '?'}</div>
          <div class="text-muted small">${m.homeTeam?.shortName || ''}</div>
        </div>
        ${scoreSection}
        <div class="text-center flex-1">
          <div class="mb-2">${awayFlag}</div>
          <div class="fw-bold">${m.awayTeam?.name || '?'}</div>
          <div class="text-muted small">${m.awayTeam?.shortName || ''}</div>
        </div>
      </div>
    </div>`;

  if (isLocked || m.status === 'FINISHED') {
    lockedNotice.classList.remove('d-none');
  }
}

// ── Betting Lines ──────────────────────────────────────────────────────

function renderBettingLines() {
  const lines = matchData.bettingLines || [];
  const isLocked = matchData.predictionLocked || matchData.status === 'FINISHED';

  if (lines.length === 0) return;

  // Nhóm theo lineType
  const groups = {};
  lines.forEach(l => {
    const g = friendlyGroupName(l.lineType);
    if (!groups[g]) groups[g] = [];
    groups[g].push(l);
  });

  let html = '';
  Object.entries(groups).forEach(([group, groupLines]) => {
    html += `<div class="betting-panel-header">${group}</div>`;
    groupLines.forEach(line => {
      const lockedClass = isLocked ? ' locked' : '';
      const resultHtml = line.result
        ? `<span class="line-result-badge ${line.result === 'WIN' ? 'bg-success' : line.result === 'LOSE' ? 'bg-danger' : 'bg-secondary'} text-white">${line.result}</span>`
        : '';
      html += `
        <div class="bet-line${lockedClass}" id="line-${line.id}"
             onclick="${isLocked ? '' : `selectLine(${line.id}, '${escapeAttr(line.description)}', ${line.odds})`}">
          <span class="bet-line-desc">${line.description}</span>
          ${resultHtml}
          <span class="bet-line-odds">${Format.odds(line.odds)}</span>
        </div>`;
    });
  });

  document.getElementById('betting-lines').innerHTML = html;
}

function friendlyGroupName(lineType) {
  const map = {
    WIN_HOME: 'Đội thắng',
    DRAW: 'Đội thắng',
    WIN_AWAY: 'Đội thắng',
    OVER: 'Tài/Xỉu',
    UNDER: 'Tài/Xỉu',
    BTTS: 'Cả hai đội ghi bàn',
    EXACT_SCORE: 'Tỷ số chính xác',
    FIRST_SCORER: 'Cầu thủ ghi bàn đầu',
    SPECIAL: 'Kèo đặc biệt',
  };
  return map[lineType] || lineType;
}

function escapeAttr(s) {
  return s.replace(/'/g, "\\'");
}

// ── Bet Slip Logic ─────────────────────────────────────────────────────

function selectLine(lineId, description, odds) {
  if (matchData.predictionLocked || matchData.status === 'FINISHED') return;

  // Bỏ highlight cũ
  document.querySelectorAll('.bet-line.selected').forEach(el => el.classList.remove('selected'));
  document.getElementById(`line-${lineId}`)?.classList.add('selected');

  selectedLine = { lineId, description, odds };
  document.getElementById('selected-line-desc').textContent = description;
  document.getElementById('selected-line-odds').textContent = `x${Format.odds(odds)}`;

  betSlipSection.style.display = 'block';
  betAmountInput.focus();
  updatePotentialWin();
}

betAmountInput.addEventListener('input', updatePotentialWin);

function updatePotentialWin() {
  const amount = parseInt(betAmountInput.value, 10);
  if (!selectedLine || isNaN(amount) || amount <= 0) {
    document.getElementById('potential-win').textContent = '—';
    return;
  }
  const win = Math.floor(amount * selectedLine.odds);
  document.getElementById('potential-win').textContent = Format.credits(win);
}

function setQuickBet(amount) {
  betAmountInput.value = amount;
  updatePotentialWin();
}

function setMaxBet() {
  const user = Storage.getUser();
  if (user) { betAmountInput.value = user.credits; updatePotentialWin(); }
}

async function placeBet() {
  if (!selectedLine) { Toast.warning('Vui lòng chọn kèo trước.'); return; }

  const amount = parseInt(betAmountInput.value, 10);
  if (isNaN(amount) || amount < 10) {
    Toast.warning('Số xu tối thiểu là 10.');
    return;
  }

  const existingTotal = getTotalBetForLine(selectedLine.lineId);
  if (existingTotal > 0) {
    const confirmed = await confirmRepeatBet(existingTotal, amount);
    if (!confirmed) return;
  }

  const btn = document.getElementById('place-bet-btn');
  btn.disabled = true;
  btn.textContent = 'Đang xử lý...';

  try {
    await Api.post('/api/v1/predictions', {
      bettingLineId: selectedLine.lineId,
      creditBet: amount,
    });

    Toast.success(`Đặt cược thành công! Trừ ${Format.credits(amount)}.`);

    // Reset bet slip
    selectedLine = null;
    betSlipSection.style.display = 'none';
    betAmountInput.value = '';
    document.querySelectorAll('.bet-line.selected').forEach(el => el.classList.remove('selected'));

    // Refresh predictions list
    await renderMyPredictions();

    // Cập nhật credit trên navbar
    try {
      const profile = await Api.get('/api/v1/users/me');
      Storage.updateUser({ ...Storage.getUser(), credits: profile.credits });
    } catch {}

  } catch (err) {
    Toast.error(err.message || 'Đặt cược thất bại.');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Xác nhận đặt cược';
  }
}

// ── My Predictions ─────────────────────────────────────────────────────

function getTotalBetForLine(lineId) {
  return myMatchPredictions
    .filter(p => String(p.bettingLineId) === String(lineId))
    .reduce((sum, p) => sum + (Number(p.creditBet) || 0), 0);
}

function confirmRepeatBet(existingTotal, nextAmount) {
  const modalEl = document.getElementById('repeat-bet-confirm-modal');
  const messageEl = document.getElementById('repeat-bet-confirm-message');
  const confirmBtn = document.getElementById('repeat-bet-confirm-btn');

  if (!modalEl || !messageEl || !confirmBtn) return Promise.resolve(true);

  const totalAfter = existingTotal + nextAmount;
  messageEl.innerHTML = `
    Bạn đã đặt <strong>${Format.credits(existingTotal)}</strong> cho kèo
    <strong>${escapeHtml(selectedLine.description)}</strong>.<br>
    Nếu tiếp tục đặt thêm <strong>${Format.credits(nextAmount)}</strong>,
    tổng số tiền đã đặt cho kèo này sẽ là <strong>${Format.credits(totalAfter)}</strong>.
  `;

  return new Promise(resolve => {
    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    const cleanup = () => {
      confirmBtn.removeEventListener('click', onConfirm);
      modalEl.removeEventListener('hidden.bs.modal', onCancel);
    };

    const onConfirm = () => {
      cleanup();
      modal.hide();
      resolve(true);
    };

    const onCancel = () => {
      cleanup();
      resolve(false);
    };

    confirmBtn.addEventListener('click', onConfirm);
    modalEl.addEventListener('hidden.bs.modal', onCancel, { once: true });
    modal.show();
  });
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

async function renderMyPredictions() {
  const container = document.getElementById('my-predictions');
  try {
    const data = await Api.get(`/api/v1/users/me/predictions?page=0&size=1000`);
    const forThisMatch = (data.content || []).filter(p => p.matchId == matchId);
    myMatchPredictions = forThisMatch;

    if (forThisMatch.length === 0) {
      myMatchPredictions = [];
      container.innerHTML = '<div class="text-muted small text-center py-2">Chưa có dự đoán nào.</div>';
      return;
    }

    container.innerHTML = forThisMatch.map(p => `
      <div class="prediction-row">
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <div class="small fw-semibold">${p.bettingLineDescription || '—'}</div>
            <div class="small text-muted">Đặt: ${Format.credits(p.creditBet)}</div>
          </div>
          <div class="text-end">
            ${Format.predictionStatusBadge(p.status)}
            ${p.creditResult !== null
              ? `<div class="${p.creditResult >= 0 ? 'credit-change-positive' : 'credit-change-negative'} small mt-1">
                   ${p.creditResult >= 0 ? '+' : ''}${Format.credits(p.creditResult)}
                 </div>`
              : ''}
          </div>
        </div>
      </div>`).join('');
  } catch {
    myMatchPredictions = [];
    container.innerHTML = '<div class="text-muted small text-center py-2">Không tải được dự đoán.</div>';
  }
}

// ── Init ───────────────────────────────────────────────────────────────
init();
