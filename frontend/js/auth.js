// ============================================
// GradeSecure — Authentication
// ============================================

(function checkAuth() {
    const user = sessionStorage.getItem('gs_user');
    if (user) {
        const loc = window.location.pathname;
        if (loc.endsWith('index.html') || loc === '/' || loc.endsWith('/frontend/')) {
            window.location.href = 'dashboard.html';
        }
    }
})();

document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorDiv = document.getElementById('loginError');
    const errorText = document.getElementById('loginErrorText');
    const btn = document.getElementById('loginBtn');

    errorDiv.style.display = 'none';
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Signing in...';

    try {
        const response = await api.login(username, password);
        sessionStorage.setItem('gs_user', JSON.stringify(response));
        window.location.href = 'dashboard.html';
    } catch (err) {
        errorText.textContent = err.message || 'Invalid credentials. Please try again.';
        errorDiv.style.display = 'flex';
    } finally {
        btn.disabled = false;
        btn.textContent = 'Sign in';
    }
});

function logout() {
    sessionStorage.removeItem('gs_user');
    window.location.href = 'index.html';
}

function getCurrentUser() {
    const data = sessionStorage.getItem('gs_user');
    return data ? JSON.parse(data) : null;
}

function requireLogin() {
    const user = getCurrentUser();
    if (!user) { window.location.href = 'index.html'; return null; }
    return user;
}

function initSidebar(user, activePage) {
    document.getElementById('sidebarUserName').textContent = user.fullName;
    document.getElementById('sidebarUserRole').textContent = user.role;
    const initials = user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2);
    document.getElementById('userInitials').textContent = initials;
    const activeLink = document.querySelector(`[data-page="${activePage}"]`);
    if (activeLink) activeLink.classList.add('active');
}
