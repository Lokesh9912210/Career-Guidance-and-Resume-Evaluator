requireAuth();
const catColors = ['linear-gradient(135deg,#667eea,#764ba2)','linear-gradient(135deg,#f093fb,#f5576c)','linear-gradient(135deg,#4facfe,#00f2fe)','linear-gradient(135deg,#43e97b,#38f9d7)','linear-gradient(135deg,#fa709a,#fee140)','linear-gradient(135deg,#a18cd1,#fbc2eb)'];

async function getScore() {
    showLoading('Analyzing...');
    document.getElementById('scoreBtn').disabled = true;
    try {
        const data = await apiCall('/api/score/resume');
        hideLoading();
        if (data.success) displayScore(data);
        else { if (data.message && data.message.includes('No resume')) document.getElementById('noResumeState').style.display='block'; showToast(data.message||'Failed','error'); }
    } catch (e) { hideLoading(); showToast('Error','error'); }
    document.getElementById('scoreBtn').disabled = false;
}

function displayScore(data) {
    document.getElementById('scoreResults').style.display = 'block';
    document.getElementById('overallScore').textContent = data.overallScore;
    document.getElementById('scoreSummary').textContent = data.summary;

    const maxScores = {'Technical Skills':25,'Experience':20,'Education':15,'Contact Info':10,'Formatting':10,'Keywords':10};
    let i = 0;
    document.getElementById('categoryBreakdown').innerHTML = Object.entries(data.categoryScores||{}).map(([cat,score]) => {
        const max = maxScores[cat]||25;
        const pct = Math.min(100, (score/max)*100);
        const color = catColors[i++ % catColors.length];
        return '<div style="display:flex;align-items:center;gap:16px"><span class="category-label">'+cat+'</span><div class="category-bar"><div class="category-bar-fill" style="width:'+pct+'%;background:'+color+'"></div></div><span class="category-score">'+score+'</span></div>';
    }).join('');

    document.getElementById('strengthsList').innerHTML = (data.strengths||[]).map(s => '<li>💪 '+s+'</li>').join('') || '<li>Upload a more detailed resume</li>';
    document.getElementById('improvementsList').innerHTML = (data.improvements||[]).map(s => '<li>🔧 '+s+'</li>').join('') || '<li>Looking good!</li>';
}

async function sendToEmail() {
    showLoading('Sending...');
    try {
        const data = await apiCall('/api/score/send-results', 'POST');
        hideLoading();
        showToast(data.message || 'Sent!', data.success ? 'success' : 'error');
    } catch (e) { hideLoading(); showToast('Error', 'error'); }
}