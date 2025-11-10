// Simple manejo del sidebar y submenus
document.addEventListener('DOMContentLoaded', function () {
  const sidebar = document.getElementById('sidebar');
  const sidebarToggle = document.getElementById('sidebarToggle');
  const mobileBtn = document.getElementById('mobileMenuBtn');

  // Toggle submenus
  document.querySelectorAll('.submenu-toggle').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      const parent = btn.closest('.has-sub');
      const open = parent.classList.toggle('open');
      // opcional: cerrar otros submenus
      // document.querySelectorAll('.has-sub').forEach(h => { if(h !== parent) h.classList.remove('open'); });
    });
  });

  // Toggle fixed sidebar (desktop)
  if (sidebarToggle) {
    sidebarToggle.addEventListener('click', () => {
      // en mobile se usa class .open
      if (window.innerWidth <= 900) {
        sidebar.classList.toggle('open');
      } else {
        // versus "compact" behaviour: cambia ancho con clase compact (ejemplo sencillo)
        if (sidebar.classList.contains('compact')) {
          sidebar.classList.remove('compact');
          sidebar.style.width = '';
        } else {
          sidebar.classList.add('compact');
          sidebar.style.width = '72px';
        }
      }
    });
  }

  // mobile menu button
  if (mobileBtn) {
    mobileBtn.addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });
  }

  // Cerrar sidebar en click fuera (mobile)
  document.addEventListener('click', (evt) => {
    if (window.innerWidth <= 900) {
      const target = evt.target;
      if (!sidebar.contains(target) && !mobileBtn.contains(target)) {
        sidebar.classList.remove('open');
      }
    }
  });

});
