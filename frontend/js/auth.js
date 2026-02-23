document.addEventListener('DOMContentLoaded', () => {
    if (localStorage.getItem('token')) { window.location.href = 'dashboard.html'; return; }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = document.getElementById('loginBtn');
            btn.disabled = true; btn.textContent = 'Signing in...';
            try {
                const data = await apiCall('/api/auth/login', 'POST', {
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value
                });
                if (data.success) {
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('user', JSON.stringify(data.user));
                    showToast('Login successful!', 'success');
                    setTimeout(() => window.location.href = 'dashboard.html', 1000);
                } else { showToast(data.message || 'Login failed', 'error'); }
            } catch (err) { showToast('Connection error', 'error'); }
            btn.disabled = false; btn.textContent = 'Sign In →';
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = document.getElementById('registerBtn');
            btn.disabled = true; btn.textContent = 'Creating...';
            try {
                const data = await apiCall('/api/auth/register', 'POST', {
                    fullName: document.getElementById('fullName').value,
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value,
                    phone: document.getElementById('phone')?.value || '',
                    telegramChatId: document.getElementById('telegramChatId')?.value || ''
                });
                if (data.success) {
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('user', JSON.stringify(data.user));
                    showToast('Account created!', 'success');
                    setTimeout(() => window.location.href = 'dashboard.html', 1000);
                } else { showToast(data.message || 'Failed', 'error'); }
            } catch (err) { showToast('Connection error', 'error'); }
            btn.disabled = false; btn.textContent = 'Create Account →';
        });
    }
});