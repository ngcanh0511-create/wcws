INSERT INTO achievements (code, name, description, icon, badge_color, condition_type, condition_value) VALUES
    ('PROPHET',     'Thánh Tiên Tri',       'Đoán đúng 10 trận liên tiếp',              '🔮', '#9B59B6', 'STREAK_WIN',     10),
    ('SCORE_SNIPER','Bắn Tỉa Tỷ Số',        'Đoán đúng tỷ số chính xác 5 lần',          '🎯', '#E74C3C', 'EXACT_SCORE_WIN', 5),
    ('BAD_LUCK_KING','Thánh Nhọ',            'Sai 10 trận liên tiếp',                     '💀', '#2C3E50', 'STREAK_LOSE',    10),
    ('CONTRARIAN',  'Ngược Chiều Vũ Trụ',   'Đoán ngược >80% người, thắng 5 lần',       '🙃', '#E67E22', 'CONTRARIAN_WIN',  5),
    ('HIGH_ROLLER', 'Tất Tay',              'Đặt toàn bộ credit vào 1 kèo và thắng',    '🎲', '#F39C12', 'ALL_IN_WIN',      1),
    ('BROKE',       'Người Ăn Mày',         'Credit về 0 lần đầu tiên',                  '😭', '#95A5A6', 'CREDIT_ZERO',     0),
    ('PHOENIX',     'Phượng Hoàng Tái Sinh','Từ dưới 100 credit comeback lên top 3',     '🦅', '#E74C3C', 'PHOENIX',         1),
    ('TYCOON',      'Đại Gia',              'Tích lũy đủ 10,000 credit',                 '💰', '#F1C40F', 'CREDIT_REACH', 10000),
    ('LUCKY_STAR',  'Ngôi Sao May Mắn',     'Thắng 3 kèo có odds trên 5.0',             '⭐', '#1ABC9C', 'HIGH_ODDS_WIN',   3),
    ('CHAMPION',    'Nhà Vô Địch',          'Đứng #1 BXH khi giải kết thúc',             '🏆', '#FFD700', 'TOP_RANK_FINAL',  1);
