requireAuth();

async function searchOccupations() {
    const keyword = document.getElementById('occupationSearch').value.trim();
    if (!keyword) { showToast('Enter a keyword', 'error'); return; }
    showLoading('Searching...');
    try {
        const data = await apiCall('/api/roadmap/search-occupations?keyword=' + encodeURIComponent(keyword));
        hideLoading();
        if (data.success && data.occupations.length) {
            document.getElementById('occupationList').innerHTML = data.occupations.map(o =>
                '<div class="occupation-item" onclick="selectOccupation(\'' + o.code + '\',\'' + o.title.replace(/'/g,"\\'") + '\')" id="occ-' + o.code.replace(/\./g,'-') + '"><div><strong>' + o.title + '</strong><div class="onet-code">' + o.code + '</div></div><span>→</span></div>'
            ).join('');
        } else document.getElementById('occupationList').innerHTML = '<p style="color:var(--text-muted);text-align:center;padding:20px">No results</p>';
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

async function selectOccupation(code, title) {
    document.querySelectorAll('.occupation-item').forEach(el => el.classList.remove('selected'));
    const el = document.getElementById('occ-' + code.replace(/\./g,'-'));
    if (el) el.classList.add('selected');
    showLoading('Generating roadmap...');
    try {
        const data = await apiCall('/api/roadmap/generate', 'POST', { occupation: title, onetCode: code });
        hideLoading();
        if (data.success) { displayRoadmap(data); showToast('Roadmap ready!', 'success'); }
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}

function displayRoadmap(data) {
    document.getElementById('roadmapResults').style.display = 'block';
    document.getElementById('targetOcc').textContent = data.targetOccupation;
    document.getElementById('currentSkillsCount').textContent = (data.userCurrentSkills||[]).length;
    document.getElementById('missingSkillsCount').textContent = (data.missingSkills||[]).length;
    document.getElementById('missingSkillsList').innerHTML = (data.missingSkills||[]).map(s => '<span class="skill-tag missing">📚 ' + s + '</span>').join('') || '<p style="color:#43e97b">You have all skills!</p>';
    document.getElementById('roadmapTimeline').innerHTML = (data.roadmapSteps||[]).map((step,i) =>
        '<div class="roadmap-step" style="animation-delay:'+i*0.1+'s"><div class="step-card"><div class="step-header"><span class="step-number">Step '+step.stepNumber+'</span><span class="step-difficulty '+(step.difficulty||'intermediate').toLowerCase()+'">'+step.difficulty+'</span></div><h4>'+step.skillName+'</h4><p>'+step.description+'</p><div class="step-meta"><span>⏱️ '+step.estimatedTime+'</span></div>'+(step.resources?'<div class="step-resources"><h5>📌 Resources:</h5><ul>'+step.resources.map(r=>'<li>'+r+'</li>').join('')+'</ul></div>':'')+'</div></div>'
    ).join('') || '<p style="text-align:center;color:var(--text-muted)">No steps needed!</p>';
    document.getElementById('roadmapResults').scrollIntoView({ behavior: 'smooth' });
}

document.getElementById('occupationSearch')?.addEventListener('keypress', e => { if (e.key==='Enter') searchOccupations(); });