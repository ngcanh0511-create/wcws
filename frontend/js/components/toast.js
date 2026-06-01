/**
 * Toast notification — hiện ở góc trên phải.
 * Dùng: Toast.success('Đặt kèo thành công!') hoặc Toast.error('Lỗi mạng')
 */
const Toast = {
  _container: null,

  _getContainer() {
    if (!this._container) {
      this._container = document.createElement('div');
      this._container.id = 'toast-container';
      this._container.className = 'toast-container position-fixed top-0 end-0 p-3';
      this._container.style.zIndex = '9999';
      document.body.appendChild(this._container);
    }
    return this._container;
  },

  _show(message, type) {
    const id = `toast-${Date.now()}`;
    const iconMap = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
    const bgMap = {
      success: 'text-bg-success',
      error: 'text-bg-danger',
      warning: 'text-bg-warning',
      info: 'text-bg-primary',
    };

    const html = `
      <div id="${id}" class="toast align-items-center ${bgMap[type] || 'text-bg-secondary'} border-0" role="alert">
        <div class="d-flex">
          <div class="toast-body">
            <strong>${iconMap[type] || ''}</strong> ${message}
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
      </div>`;

    this._getContainer().insertAdjacentHTML('beforeend', html);
    const el = document.getElementById(id);
    const toast = new bootstrap.Toast(el, { delay: 4000 });
    toast.show();
    el.addEventListener('hidden.bs.toast', () => el.remove());
  },

  success: (msg) => Toast._show(msg, 'success'),
  error: (msg) => Toast._show(msg, 'error'),
  warning: (msg) => Toast._show(msg, 'warning'),
  info: (msg) => Toast._show(msg, 'info'),
};
