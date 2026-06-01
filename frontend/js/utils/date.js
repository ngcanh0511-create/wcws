const DateUtils = {
  // "2026-06-15T18:00:00" → "15/06 18:00"
  formatMatchTime(iso) {
    const d = new Date(iso);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const hour = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${day}/${month} ${hour}:${min}`;
  },

  // "2026-06-15T18:00:00" → "15 tháng 6, 2026"
  formatLong(iso) {
    return new Date(iso).toLocaleDateString('vi-VN', {
      day: 'numeric', month: 'long', year: 'numeric',
    });
  },

  // Relative time: "5 phút trước", "2 giờ trước"
  relative(iso) {
    const diff = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'vừa xong';
    if (mins < 60) return `${mins} phút trước`;
    const hours = Math.floor(mins / 60);
    if (hours < 24) return `${hours} giờ trước`;
    const days = Math.floor(hours / 24);
    return `${days} ngày trước`;
  },

  isPast(iso) {
    return new Date(iso) < new Date();
  },
};
