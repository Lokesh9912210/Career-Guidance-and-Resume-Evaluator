requireAuth();
let selectedFile = null;

document.addEventListener('DOMContentLoaded', () => {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('fileInput');

    uploadArea.addEventListener('dragover', e => { e.preventDefault(); uploadArea.classList.add('dragover'); });
    uploadArea.addEventListener('dragleave', () => uploadArea.classList.remove('dragover'));
    uploadArea.addEventListener('drop', e => { e.preventDefault(); uploadArea.classList.remove('dragover'); if (e.dataTransfer.files.length) handleFileSelect(e.dataTransfer.files[0]); });
    fileInput.addEventListener('change', e => { if (e.target.files.length) handleFileSelect(e.target.files[0]); });
});

function handleFileSelect(file) {
    const ext = '.' + file.name.split('.').pop().toLowerCase();
    if (!['.pdf','.docx','.txt'].includes(ext)) { showToast('Upload PDF, DOCX, or TXT', 'error'); return; }
    if (file.size > 10*1024*1024) { showToast('Max 10MB', 'error'); return; }
    selectedFile = file;
    document.getElementById('fileInfo').style.display = 'block';
    document.getElementById('selectedFileName').textContent = '📄 ' + file.name;
    document.getElementById('selectedFileSize').textContent = 'Size: ' + (file.size/1024).toFixed(1) + ' KB';
    document.getElementById('uploadArea').innerHTML = '<span class="upload-icon">✅</span><h3>' + file.name + '</h3><p>Click to change</p>';
}

async function uploadResume() {
    if (!selectedFile) { showToast('Select a file first', 'error'); return; }
    document.getElementById('uploadBtn').disabled = true;
    showLoading('Analyzing resume...');
    const formData = new FormData();
    formData.append('file', selectedFile);
    try {
        const data = await apiCall('/api/resume/upload', 'POST', formData, true);
        hideLoading();
        if (data.success) { showToast('Resume analyzed!', 'success'); displayResults(data); }
        else showToast(data.message || 'Failed', 'error');
    } catch (err) { hideLoading(); showToast('Error uploading', 'error'); }
    document.getElementById('uploadBtn').disabled = false;
}

function displayResults(data) {
    document.getElementById('results').style.display = 'block';
    const sc = document.getElementById('extractedSkills');
    if (data.extractedSkills && data.extractedSkills.length) {
        sc.innerHTML = data.extractedSkills.map(s => '<span class="skill-tag user">' + s + '</span>').join('');
        document.getElementById('skillCountBadge').textContent = data.extractedSkills.length + ' skills';
    } else sc.innerHTML = '<p style="color:var(--text-muted)">No skills found</p>';
    document.getElementById('extractedText').textContent = data.extractedText || 'No text';
    document.getElementById('results').scrollIntoView({ behavior: 'smooth' });
}