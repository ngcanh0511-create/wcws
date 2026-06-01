if (!AuthGuard.require()) throw new Error('Not authenticated');

Navbar.render('dashboard');

const matchList = document.getElementById('match-list');
const miniLb = document.getElementById('mini-leaderboard');
const actFeed = document.getElementById('activity-feed');
const filterDate = document.getElementById('filter-date');
const filterStatus = document.getElementById('filter-status');
const creditDisplay = document.getElementById('credit-display');
const heroTotalMatches = document.getElementById('hero-total-matches');
const heroNextMatch = document.getElementById('hero-next-match');
const heroLiveCount = document.getElementById('hero-live-count');

// Hiển thị credit từ localStorage (realtime refresh sau)
const user = Storage.getUser();
if (user) creditDisplay.textContent = Format.credits(user.credits);

let allMatches = [];
let currentPage = 0;
const PAGE_SIZE = 10;

async function loadAll() {
  await Promise.all([loadMatches(), loadLeaderboard(), loadFeed()]);
  // Refresh credit từ server
  try {
    const profile = await Api.get('/api/v1/users/me');
    creditDisplay.textContent = Format.credits(profile.credits);
    Storage.updateUser({ ...user, credits: profile.credits });
  } catch {}
}

// ── Matches ────────────────────────────────────────────────────────────

async function loadMatches() {
  try {
    const data = await Api.get('/api/v1/matches');
    allMatches = data;
    renderHeroStats();
    renderMatches();
  } catch (err) {
    matchList.innerHTML = `<div class="alert alert-danger">Không tải được lịch thi đấu: ${err.message}</div>`;
  }
}

function renderHeroStats() {
  const upcoming = allMatches
    .filter(m => m.status === 'SCHEDULED')
    .sort((a, b) => String(a.matchDate).localeCompare(String(b.matchDate)));
  const liveCount = allMatches.filter(m => m.status === 'LIVE').length;

  if (heroTotalMatches) heroTotalMatches.textContent = allMatches.length;
  if (heroLiveCount) heroLiveCount.textContent = liveCount;
  if (heroNextMatch) {
    heroNextMatch.textContent = upcoming[0]?.matchDate
      ? DateUtils.formatMatchTime(upcoming[0].matchDate)
      : '--';
  }
}

function renderMatches() {
  const date = filterDate.value;
  const status = filterStatus.value;

  let filtered = allMatches;
  if (date) filtered = filtered.filter(m => m.matchDate?.startsWith(date));
  if (status) filtered = filtered.filter(m => m.status === status);

  if (filtered.length === 0) {
    matchList.innerHTML = '<div class="text-center py-5 text-muted">Không có trận đấu nào.</div>';
    document.getElementById('pagination').innerHTML = '';
    return;
  }

  // Nhóm theo ngày
  const grouped = {};
  filtered.forEach(m => {
    const dateKey = m.matchDate ? m.matchDate.substring(0, 10) : 'unknown';
    if (!grouped[dateKey]) grouped[dateKey] = [];
    grouped[dateKey].push(m);
  });

  let html = '';
  Object.entries(grouped).sort(([a], [b]) => a.localeCompare(b)).forEach(([dateKey, matches]) => {
    const label = new Date(dateKey).toLocaleDateString('vi-VN', {
      weekday: 'long', day: 'numeric', month: 'long'
    });
    html += `<div class="text-muted small fw-semibold mb-2 mt-3 text-uppercase">${label}</div>`;
    matches.forEach(m => {
      html += renderMatchCard(m);
    });
  });

  matchList.innerHTML = html;
}

function renderMatchCard(m) {
  const homeFlag = m.homeTeam?.flagUrl
    ? `<img src="${m.homeTeam.flagUrl}" class="team-flag" alt="${m.homeTeam.name}" onerror="this.style.display='none'">`
    : '<i class="bi bi-flag team-flag d-inline-flex align-items-center justify-content-center"></i>';
  const awayFlag = m.awayTeam?.flagUrl
    ? `<img src="${m.awayTeam.flagUrl}" class="team-flag" alt="${m.awayTeam.name}" onerror="this.style.display='none'">`
    : '<i class="bi bi-flag team-flag d-inline-flex align-items-center justify-content-center"></i>';

  const score = m.status === 'FINISHED'
    ? `<span class="match-score">${Format.score(m.homeScore, m.awayScore)}</span>`
    : `<div class="text-center">
         <div class="match-score text-muted">VS</div>
         <div class="small text-muted">${DateUtils.formatMatchTime(m.matchDate)}</div>
       </div>`;

  const stageBadge = m.stage ? `<span class="ms-2">${Format.stageBadge(m.stage)}</span>` : '';

  return `
    <a href="/pages/match.html?id=${m.id}" class="match-card mb-3">
      <div class="d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center gap-2 flex-1">
          ${homeFlag}
          <span class="team-name">${m.homeTeam?.name || '?'}</span>
        </div>
        ${score}
        <div class="d-flex align-items-center gap-2 flex-1 justify-content-end">
          <span class="team-name text-end">${m.awayTeam?.name || '?'}</span>
          ${awayFlag}
        </div>
      </div>
      <div class="d-flex align-items-center mt-2 gap-2">
        ${Format.matchStatusBadge(m.status)}
        ${stageBadge}
        ${m.bettingLines?.length > 0
          ? `<span class="badge bg-success bg-opacity-25 text-success ms-auto">${m.bettingLines.length} kèo</span>`
          : ''}
      </div>
    </a>`;
}

// ── Leaderboard mini ───────────────────────────────────────────────────

async function loadLeaderboard() {
  try {
    const data = await Api.get('/api/v1/leaderboard');
    const top5 = data.slice(0, 5);
    if (top5.length === 0) {
      miniLb.innerHTML = '<div class="text-muted small text-center py-2">Chưa có dữ liệu</div>';
      return;
    }

    miniLb.innerHTML = top5.map((entry, i) => {
      const rank = i + 1;
      const rankClass = rank <= 3 ? `rank-${rank}` : 'rank-other';
      const defaultAvatar = '/assets/avatars/defaults/default_1.png';
      const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
      const avatarSrc = entry.avatarUrl
        ? `${apiBase}${entry.avatarUrl}`
        : defaultAvatar;

      return `
        <div class="d-flex align-items-center gap-2 py-2 border-bottom" style="border-color:var(--wcpl-border) !important">
          <div class="rank-badge ${rankClass}">${rank}</div>
          <img src="${avatarSrc}" width="28" height="28" class="rounded-circle"
               onerror="this.onerror=null;this.src='${defaultAvatar}'" alt="">
          <span class="small flex-1 text-truncate">${entry.displayName}</span>
          <span class="small text-warning fw-bold">${Format.credits(entry.credits)}</span>
        </div>`;
    }).join('');
  } catch {}
}

// ── Activity Feed ──────────────────────────────────────────────────────

async function loadFeed() {
  try {
    const data = await Api.get('/api/v1/activity-feed?limit=10');
    if (data.length === 0) {
      actFeed.innerHTML = '<div class="text-muted small text-center">Chưa có hoạt động nào.</div>';
      return;
    }
    actFeed.innerHTML = data.map(item => `
      <div class="feed-item">
        <span class="text-muted small">${DateUtils.relative(item.createdAt)}</span>
        <div class="small mt-1">${item.message}</div>
      </div>`).join('');
  } catch {}
}

// ── Filters ────────────────────────────────────────────────────────────

filterDate.addEventListener('change', renderMatches);
filterStatus.addEventListener('change', renderMatches);

// ── Init ───────────────────────────────────────────────────────────────

loadAll();
