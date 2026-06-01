const Format = {
  // 1500 → "1,500 xu"
  credits(n) {
    return `${Number(n).toLocaleString('vi-VN')} xu`;
  },

  // 1.85 → "1.85"  (odds luôn 2 chữ số thập phân)
  odds(n) {
    return Number(n).toFixed(2);
  },

  // "WIN" → badge màu xanh, "LOSE" → đỏ, "PENDING" → vàng, "VOID" → xám
  predictionStatusBadge(status) {
    const map = {
      WIN: ['bg-success', 'Thắng'],
      LOSE: ['bg-danger', 'Thua'],
      PENDING: ['bg-warning text-dark', 'Chờ kết quả'],
      VOID: ['bg-secondary', 'Hủy'],
    };
    const [cls, label] = map[status] || ['bg-secondary', status];
    return `<span class="badge ${cls}">${label}</span>`;
  },

  // "SCHEDULED" | "LIVE" | "FINISHED"
  matchStatusBadge(status) {
    const map = {
      SCHEDULED: ['bg-primary', 'Sắp diễn ra'],
      LIVE: ['bg-danger', '● LIVE'],
      FINISHED: ['bg-secondary', 'Kết thúc'],
    };
    const [cls, label] = map[status] || ['bg-secondary', status];
    return `<span class="badge ${cls}">${label}</span>`;
  },

  stageLabel(stage) {
    const map = {
      GROUP: 'Vòng bảng',
      R32: 'Vòng 1/32',
      R16: 'Vòng 1/16',
      QF: 'Tứ kết',
      SF: 'Bán kết',
      FINAL: 'Chung kết',
      THIRD_PLACE: 'Tranh hạng ba',
    };
    return map[stage] || stage || '';
  },

  stageColorClass(stage) {
    const map = {
      GROUP: 'bg-info text-dark',
      R32: 'bg-primary',
      R16: 'bg-primary',
      QF: 'bg-warning text-dark',
      SF: 'bg-danger',
      FINAL: 'bg-success',
      THIRD_PLACE: 'bg-secondary',
    };
    return map[stage] || 'bg-secondary';
  },

  stageBadge(stage) {
    if (!stage) return '';
    return `<span class="badge ${this.stageColorClass(stage)}">${this.stageLabel(stage)}</span>`;
  },

  // Tỷ số: null → "-"
  score(home, away) {
    if (home === null || home === undefined) return '-';
    return `${home} - ${away}`;
  },
};
