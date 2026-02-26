const API_BASE =  'https://career-guidance-and-resume-evaluator-production-2d2f.up.railway.app/api/auth';

function requireAuth() {
    if (!localStorage.getItem('token')) window.location.href = 'login.html';
}

async function apiCall(endpoint, method = 'GET', body = null, isFormData = false) {
    const token = localStorage.getItem('token');
    const headers = {};
    if (token) headers['Authorization'] = 'Bearer ' + token;
    if (!isFormData) headers['Content-Type'] = 'application/json';

    const options = { method, headers };
    if (body) options.body = isFormData ? body : JSON.stringify(body);

    const response = await fetch(API_BASE + endpoint, options);
    if (response.status === 401 || response.status === 403) {
        localStorage.clear();
        window.location.href = 'login.html';
        throw new Error('Unauthorized');
    }
    return await response.json();
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    toast.innerHTML = '<span>' + (icons[type]||'ℹ️') + '</span><span>' + message + '</span><span class="toast-close" onclick="this.parentElement.remove()">✕</span>';
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
}

function showLoading(text) {
    const o = document.getElementById('loadingOverlay');
    if (o) { o.style.display = 'flex'; const p = o.querySelector('p'); if (p) p.textContent = text || 'Loading...'; }
}

function hideLoading() {
    const o = document.getElementById('loadingOverlay');
    if (o) o.style.display = 'none';
}

function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

function formatSalary(min, max, currency) {
    if ((!min || min === 'N/A') && (!max || max === 'N/A')) return 'Not specified';
    const fmt = n => { const num = parseFloat(n); return isNaN(num) ? 'N/A' : '$' + num.toLocaleString(); };
    if (min && min !== 'N/A' && max && max !== 'N/A') return fmt(min) + ' - ' + fmt(max);
    if (min && min !== 'N/A') return 'From ' + fmt(min);
    return 'Up to ' + fmt(max);
}

function truncate(text, length) {
    if (!text) return '';
    return text.length > (length||200) ? text.substring(0, length||200) + '...' : text;
}

function stripHtml(html) {
    const tmp = document.createElement('div');
    tmp.innerHTML = html;
    return tmp.textContent || '';
}