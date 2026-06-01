if (!AuthGuard.require()) throw new Error('Not authenticated');
Navbar.render('leaderboard');

const currentUserId = Storage.getUser()?.id;

async function load() {
  try {
    const data = await Api.get('/api/v1/leaderboard');
    renderPodium(data.slice(0, 3));
    renderTable(data);
  } catch (err) {
    document.getElementById('leaderboard-table').innerHTML =
      `<div class="alert alert-danger m-2">Khong tai duoc: ${err.message}</div>`;
  }
}

function renderPodium(top3) {
  const order = [top3[1], top3[0], top3[2]].filter(Boolean);
  const heights = [top3[1] ? '80px' : '0', '100px', top3[2] ? '60px' : '0'];
  const medals = [
    '<i class="bi bi-award-fill"></i>',
    '<i class="bi bi-trophy-fill"></i>',
    '<i class="bi bi-award"></i>',
  ];
  const colors = ['bg-secondary', 'bg-warning', 'bg-danger'];

  const html = order.map((entry, i) => {
    if (!entry) return '';
    const defaultAvatar = '/assets/avatars/defaults/default_1.png';
    const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
    const avatarSrc = entry.avatarUrl
      ? `${apiBase}${entry.avatarUrl}`
      : defaultAvatar;
    const isMe = entry.userId == currentUserId;

    return `
      <div class="col-4 text-center">
        <div class="mb-2">
          <img src="${avatarSrc}" width="52" height="52" class="rounded-circle border border-2 ${isMe ? 'border-warning' : 'border-secondary'}"
               onerror="this.onerror=null;this.src='${defaultAvatar}'" alt="">
        </div>
        <div class="small fw-bold ${isMe ? 'text-warning' : ''}">${entry.displayName}</div>
        <div class="small text-muted">${Format.credits(entry.credits)}</div>
        <div class="mt-2 d-flex align-items-end justify-content-center">
          <div class="${colors[i]} rounded-top d-flex align-items-center justify-content-center fw-bold"
               style="width:80px; height:${heights[i]}; font-size:1.5rem">
            ${medals[i]}
          </div>
        </div>
      </div>`;
  });

  document.getElementById('podium').innerHTML = html.join('');
}

function renderTable(data) {
  if (data.length === 0) {
    document.getElementById('leaderboard-table').innerHTML =
      '<div class="text-center py-4 text-muted">Chua co du lieu.</div>';
    return;
  }

  const rows = data.map((entry, i) => {
    const rank = i + 1;
    const rankClass = rank <= 3 ? `rank-${rank}` : 'rank-other';
    const isMe = entry.userId == currentUserId;
    const defaultAvatar = '/assets/avatars/defaults/default_1.png';
    const apiBase = window.APP_CONFIG?.apiBase || 'http://localhost:8080';
    const avatarSrc = entry.avatarUrl
      ? `${apiBase}${entry.avatarUrl}`
      : defaultAvatar;

    const winRate = entry.totalBets > 0
      ? ((entry.wins / entry.totalBets) * 100).toFixed(1) + '%'
      : '-';

    return `
      <div class="d-flex align-items-center gap-3 py-2 px-3 border-bottom ${isMe ? 'bg-warning bg-opacity-10 rounded' : ''}"
           style="border-color:var(--wcpl-border) !important">
        <div class="rank-badge ${rankClass}">${rank}</div>
        <img src="${avatarSrc}" width="36" height="36" class="rounded-circle"
             onerror="this.onerror=null;this.src='${defaultAvatar}'" alt="">
        <div class="flex-1">
          <div class="fw-semibold ${isMe ? 'text-warning' : ''}">
            ${entry.displayName}${isMe ? ' <span class="badge bg-warning text-dark">Ban</span>' : ''}
          </div>
          <div class="small text-muted">${entry.wins || 0}W / ${entry.totalBets || 0} keo · ${winRate}</div>
        </div>
        <div class="text-end">
          <div class="fw-bold text-warning">${Format.credits(entry.credits)}</div>
        </div>
      </div>`;
  });

  document.getElementById('leaderboard-table').innerHTML = rows.join('');
}

load();
