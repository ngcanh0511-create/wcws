if (!AuthGuard.requireAdmin()) throw new Error('Not admin');
Navbar.render('admin');

async function load() {
  try {
    const data = await Api.get('/api/v1/admin/dashboard');
    document.getElementById('stat-users').textContent = data.totalUsers ?? '—';
    document.getElementById('stat-matches').textContent = data.totalMatches ?? '—';
    document.getElementById('stat-upcoming').textContent = data.upcomingMatches ?? '—';
    document.getElementById('stat-finished').textContent = data.finishedMatches ?? '—';
  } catch (err) {
    Toast.error('Lỗi load dashboard: ' + err.message);
  }
}

load();
