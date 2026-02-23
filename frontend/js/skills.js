requireAuth();

async function searchOccupations() {
    const keyword = document.getElementById('occupationSearch').value.trim();
    if (!keyword) { showToast('Enter keyword', 'error'); return; }
    showLoading('Searching...');
    try {
        const data = await apiCall('/api/roadmap/search-occupations?keyword=' + encodeURIComponent(keyword));
        hideLoading();
        if (data.success && data.occupations.length) {
            document.getElementById('occupationList').innerHTML = data.occupations.map(o =>
                '<div class="occupation-item" onclick="compareWithOccupation(\'' + o.code + '\',\'' + o.title.replace(/'/g,"\\'") + '\')" id="occ-' + o.code.replace(/\./g,'-') + '"><div><strong>' + o.title + '</strong><div class="onet-code">' + o.code + '</div></div><span class="btn btn-sm btn-primary">Compare</span></div>'
            ).join('');
        } else document.getElementById('occupationList').innerHTML = '<p style="color:var(--text-muted);text-align:center;padding:20px">No results</p>';
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

async function compareWithOccupation(code, title) {
    showLoading('Comparing...');
    try {
        const data = await apiCall('/api/skills/compare', 'POST', { occupation: title, onetCode: code });
        hideLoading();
        if (data.success) { displayComparison(data); showToast('Done!', 'success'); }
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

function displayComparison(data) {
    document.getElementById('comparisonResults').style.display = 'block';
    document.getElementById('compOccName').textContent = data.occupation;
    document.getElementById('matchPercent').textContent = data.matchPercentage + '%';
    setTimeout(() => { document.getElementById('matchBar').style.width = data.matchPercentage + '%'; }, 300);
    document.getElementById('matchedCount').textContent = (data.matchedSkills||[]).length;
    document.getElementById('matchedSkills').innerHTML = (data.matchedSkills||[]).map(s => '<span class="skill-tag matched">✅ ' + s + '</span>').join('') || '<p style="color:var(--text-muted)">None</p>';
    document.getElementById('missingCount').textContent = (data.missingSkills||[]).length;
    document.getElementById('missingSkills').innerHTML = (data.missingSkills||[]).map(s => '<span class="skill-tag missing">❌ ' + s + '</span>').join('') || '<p style="color:#43e97b">All matched!</p>';
    document.getElementById('extraCount').textContent = (data.extraSkills||[]).length;
    document.getElementById('extraSkills').innerHTML = (data.extraSkills||[]).map(s => '<span class="skill-tag extra">➕ ' + s + '</span>').join('') || '<p style="color:var(--text-muted)">None</p>';
    document.getElementById('comparisonResults').scrollIntoView({ behavior: 'smooth' });
}

document.getElementById('occupationSearch')?.addEventListener('keypress', e => { if (e.key==='Enter') searchOccupations(); });