if (!AuthGuard.require()) throw new Error('Not authenticated');
Navbar.render('achievements');

async function load() {
  try {
    const mine = await Api.get('/api/v1/users/me/achievements');
    const earned = new Set(mine.map(a => a.achievementCode || a.code));

    const all = [
      { code: 'PROPHET', icon: 'bi-stars', name: 'Nha tien tri', desc: 'Thang 10 keo lien tiep' },
      { code: 'SCORE_SNIPER', icon: 'bi-bullseye', name: 'Thien xa ty so', desc: 'Doan dung ty so chinh xac 5 lan' },
      { code: 'BAD_LUCK_KING', icon: 'bi-cloud-rain', name: 'Vua xui xeo', desc: 'Thua 10 keo lien tiep' },
      { code: 'CONTRARIAN', icon: 'bi-shuffle', name: 'Thanh nguoc keo', desc: 'Thang keo ty le cao x3 lien tiep' },
      { code: 'HIGH_ROLLER', icon: 'bi-gem', name: 'High Roller', desc: 'Dat cuoc it nhat 1000 xu mot keo' },
      { code: 'BROKE', icon: 'bi-wallet2', name: 'Chay tui', desc: 'Credit ve 0' },
      { code: 'PHOENIX', icon: 'bi-arrow-up-circle', name: 'Phuc hoi ky dieu', desc: 'Tu 0 xu len 10000 xu' },
      { code: 'TYCOON', icon: 'bi-coin', name: 'Dai gia', desc: 'Tich luy 10000 xu' },
      { code: 'LUCKY_STAR', icon: 'bi-stars', name: 'Ngoi sao may man', desc: 'Thang keo ty le cao nhat ngay' },
      { code: 'CHAMPION', icon: 'bi-trophy', name: 'Nha vo dich', desc: 'Dung dau bang xep hang cuoi giai' },
    ];

    const earnedAchievements = mine.reduce((acc, a) => {
      acc[a.achievementCode || a.code] = a;
      return acc;
    }, {});

    const html = all.map(a => {
      const isEarned = earned.has(a.code);
      const ea = earnedAchievements[a.code];
      return `
        <div class="col-6 col-md-4 col-lg-3">
          <div class="achievement-card ${isEarned ? '' : 'locked'}">
            <div class="achievement-icon"><i class="bi ${a.icon}"></i></div>
            <div class="fw-bold small">${a.name}</div>
            <div class="text-muted" style="font-size:0.78rem; margin-top:0.25rem">${a.desc}</div>
            ${isEarned
              ? `<div class="badge bg-success mt-2">Dat duoc</div>
                 ${ea?.earnedAt ? `<div class="text-muted" style="font-size:0.72rem">${DateUtils.relative(ea.earnedAt)}</div>` : ''}`
              : '<div class="badge bg-secondary mt-2">Chua dat</div>'}
          </div>
        </div>`;
    }).join('');

    document.getElementById('achievement-grid').innerHTML = html;
  } catch (err) {
    document.getElementById('achievement-grid').innerHTML =
      `<div class="col-12"><div class="alert alert-danger">Loi: ${err.message}</div></div>`;
  }
}

load();
