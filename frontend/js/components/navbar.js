/**
 * Render thanh navigation Bootstrap responsive.
 * Goi Navbar.render('dashboard') de highlight active link.
 */
const Navbar = {
  render(activePage) {
    const user = Storage.getUser();
    const isAdmin = user?.role === 'ADMIN';

    const links = [
      { id: 'dashboard', href: '/pages/dashboard.html', icon: 'bi-calendar2-week', label: 'Lich thi dau' },
      { id: 'leaderboard', href: '/pages/leaderboard.html', icon: 'bi-trophy', label: 'Bang xep hang' },
      { id: 'tournament', href: '/pages/tournament.html', icon: 'bi-stars', label: 'Du doan giai' },
      { id: 'hall-of-fame', href: '/pages/hall-of-fame.html', icon: 'bi-award', label: 'Hall of Fame' },
    ];

    const adminLink = isAdmin
      ? `<li class="nav-item">
           <a class="nav-link${activePage === 'admin' ? ' active' : ''}" href="/pages/admin/dashboard.html">
             <i class="bi bi-sliders me-1"></i>Admin
           </a>
         </li>`
      : '';

    const navLinks = links.map(l => `
      <li class="nav-item">
        <a class="nav-link${activePage === l.id ? ' active' : ''}" href="${l.href}">
          <i class="bi ${l.icon} me-1"></i>${l.label}
        </a>
      </li>`).join('');

    const avatarSrc = user?.avatarUrl
      ? `http://localhost:8080${user.avatarUrl}`
      : '/assets/avatars/defaults/default_1.png';

    const html = `
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
        <div class="container-xl">
          <a class="navbar-brand fw-bold" href="/pages/dashboard.html">
            <i class="bi bi-trophy-fill me-1"></i>WCPL
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navMenu">
            <ul class="navbar-nav me-auto">
              ${navLinks}
              ${adminLink}
            </ul>
            <div class="d-flex align-items-center gap-2">
              <span class="text-warning fw-bold small">
                <i class="bi bi-coin me-1"></i>${Format.credits(user?.credits ?? 0)}
              </span>
              <div class="dropdown">
                <button class="btn btn-sm btn-outline-light dropdown-toggle d-flex align-items-center gap-2"
                        data-bs-toggle="dropdown">
                  <img src="${avatarSrc}" class="rounded-circle" width="28" height="28"
                       onerror="this.src='/assets/avatars/default_1.png'" alt="avatar">
                  ${user?.displayName || 'User'}
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li><a class="dropdown-item" href="/pages/profile.html"><i class="bi bi-person-circle me-2"></i>Ho so</a></li>
                  <li><a class="dropdown-item" href="/pages/achievements.html"><i class="bi bi-patch-check me-2"></i>Thanh tich</a></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><button class="dropdown-item text-danger" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i>Dang xuat</button></li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </nav>`;

    const placeholder = document.getElementById('navbar-placeholder');
    if (placeholder) placeholder.outerHTML = html;
    else document.body.insertAdjacentHTML('afterbegin', html);
  },
};
