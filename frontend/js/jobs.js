requireAuth();
let currentPage = 1;

async function searchJobs() {
    const what = document.getElementById('jobSearch').value.trim();
    const where = document.getElementById('jobLocation').value.trim();
    const country = document.getElementById('jobCountry').value;
    showLoading('Searching jobs...');
    try {
        const data = await apiCall('/api/jobs/search?what='+encodeURIComponent(what)+'&where='+encodeURIComponent(where)+'&country='+country+'&page='+currentPage);
        hideLoading();
        if (data.success) displayJobs(data);
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

async function searchSkillBasedJobs() {
    showLoading('Finding matching jobs...');
    try {
        const data = await apiCall('/api/jobs/skill-based?country='+document.getElementById('jobCountry').value+'&page='+currentPage);
        hideLoading();
        if (data.success) { displayJobs(data); showToast('Found ' + data.totalResults + ' jobs!', 'success'); }
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

function displayJobs(data) {
    document.getElementById('resultsInfo').style.display = 'block';
    document.getElementById('jobResultCount').textContent = data.totalResults;
    const grid = document.getElementById('jobGrid');
    if (!data.jobs||!data.jobs.length) { grid.innerHTML = '<div class="empty-state"><div class="empty-icon">🔍</div><h3>No jobs found</h3></div>'; return; }
    grid.innerHTML = data.jobs.map(j =>
        '<div class="job-card"><div class="job-card-header"><div><h3>'+j.title+'</h3><div class="job-company">'+j.company+'</div></div><span class="job-salary">'+formatSalary(j.salaryMin,j.salaryMax)+'</span></div><div class="job-meta"><span>📍 '+j.location+'</span>'+(j.contractType?'<span>📋 '+j.contractType+'</span>':'')+'</div><div class="job-description">'+stripHtml(truncate(j.description,250))+'</div><div class="job-actions">'+(j.jobUrl?'<a href="'+j.jobUrl+'" target="_blank" class="btn btn-primary btn-sm">Apply →</a>':'')+'</div></div>'
    ).join('');
    document.getElementById('pagination').style.display = 'flex';
    document.getElementById('pageInfo').textContent = 'Page ' + currentPage;
}

function changePage(d) { currentPage += d; if (currentPage<1) currentPage=1; searchJobs(); }
document.getElementById('jobSearch')?.addEventListener('keypress', e => { if (e.key==='Enter') { currentPage=1; searchJobs(); } });