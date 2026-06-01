if (!AuthGuard.require()) throw new Error('Not authenticated');
Navbar.render('hall-of-fame');

async function load() {
  try {
    const data = await Api.get('/api/v1/hall-of-fame');
    const container = document.getElementById('hof-content');

    if (!data || data.length === 0) {
      container.innerHTML = `
        <div class="wcpl-card p-5 text-center">
          <div style="font-size:3rem"><i class="bi bi-bank"></i></div>
          <h6 class="mt-3 text-muted">Chua co du lieu Hall of Fame</h6>
          <p class="text-muted small">Ket qua se duoc luu sau khi giai dau ket thuc.</p>
        </div>`;
      return;
    }

    const seasons = {};
    data.forEach(entry => {
      if (!seasons[entry.season]) seasons[entry.season] = [];
      seasons[entry.season].push(entry);
    });

    let html = '';
    Object.entries(seasons).sort(([a], [b]) => b.localeCompare(a)).forEach(([season, entries]) => {
      html += `<h6 class="text-muted fw-semibold mb-3 mt-4"><i class="bi bi-calendar-event me-2"></i>Mua giai ${season}</h6>`;
      html += `<div class="wcpl-card mb-4">`;

      entries.sort((a, b) => a.finalRank - b.finalRank).forEach(entry => {
        const medals = [
          '<i class="bi bi-trophy-fill text-warning"></i>',
          '<i class="bi bi-award-fill text-light"></i>',
          '<i class="bi bi-award text-warning"></i>',
        ];
        const medal = medals[entry.finalRank - 1] || `#${entry.finalRank}`;
        const avatarSrc = entry.user?.avatarUrl
          ? `http://localhost:8080${entry.user.avatarUrl}`
          : '/assets/avatars/default_1.png';

        html += `
          <div class="d-flex align-items-center gap-3 p-3 border-bottom"
               style="border-color:var(--wcpl-border)!important">
            <div class="fs-4">${medal}</div>
            <img src="${avatarSrc}" width="40" height="40" class="rounded-circle"
                 onerror="this.src='/assets/avatars/default_1.png'" alt="">
            <div class="flex-1">
              <div class="fw-bold">${entry.user?.displayName || '-'}</div>
            </div>
            <div class="text-end">
              <div class="fw-bold text-warning">${Format.credits(entry.finalCredits)}</div>
              <div class="small text-muted">xu cuoi mua</div>
            </div>
          </div>`;
      });

      html += '</div>';
    });

    container.innerHTML = html;
  } catch (err) {
    document.getElementById('hof-content').innerHTML =
      `<div class="alert alert-danger">${err.message}</div>`;
  }
}

load();
