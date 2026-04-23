// GradeSecure — Grades & Spreadsheet

const user = requireLogin();
if (user) { initSidebar(user, 'grades'); loadClassOptions(); }

let currentClassId = null;

async function loadClassOptions() {
    const select = document.getElementById('classSelect');
    try {
        const classes = await api.getClasses(user.role === 'TEACHER' ? user.id : null);
        classes.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = `${c.name} (${c.studentCount})`;
            select.appendChild(opt);
        });
        const params = new URLSearchParams(window.location.search);
        const classId = params.get('classId');
        if (classId) { select.value = classId; selectClass(classId); }
    } catch (err) { console.error(err); }
}

document.getElementById('classSelect')?.addEventListener('change', (e) => {
    if (e.target.value) selectClass(e.target.value);
});

function selectClass(classId) {
    currentClassId = classId;
    document.getElementById('actionBar').style.display = 'flex';
    document.getElementById('btnExport').href = api.exportCsv(classId);
    document.getElementById('rankingsCard').style.display = 'none';
    loadSpreadsheet(classId);
}

async function loadSpreadsheet(classId) {
    const container = document.getElementById('spreadsheetContainer');
    container.innerHTML = '<div class="text-center" style="padding:2rem;"><div class="spinner"></div></div>';
    try {
        const data = await api.getSpreadsheet(classId);
        const { activities, students } = data;

        if (students.length === 0) {
            container.innerHTML = `<div class="empty-state"><div class="empty-icon"><i class="bi bi-people"></i></div><h4>No students</h4><p>Add students to this class first.</p><a href="classes.html" class="btn btn-primary btn-sm">Manage class</a></div>`;
            return;
        }
        if (activities.length === 0) {
            container.innerHTML = `<div class="empty-state"><div class="empty-icon"><i class="bi bi-columns-gap"></i></div><h4>No activities</h4><p>Add an activity column to start entering grades.</p><button class="btn btn-primary btn-sm" onclick="addActivityPrompt()">Add activity</button></div>`;
            return;
        }

        let html = '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Student</th>';
        activities.forEach(a => {
            html += `<th class="text-center">${a.name} <span style="color:var(--color-gray-400);font-weight:400;">(${a.maxScore})</span>
                <button class="btn btn-ghost" style="padding:0.1rem 0.25rem;font-size:0.65rem;color:var(--color-gray-400);" onclick="deleteActivity(${a.id})" title="Delete"><i class="bi bi-x"></i></button></th>`;
        });
        html += '<th class="text-center">Average</th></tr></thead><tbody>';

        students.forEach(s => {
            html += `<tr><td class="fw-600">${s.fullName}</td>`;
            s.scores.forEach(sc => {
                const val = sc.score != null ? sc.score : '';
                html += `<td class="text-center"><input type="number" class="grade-input" value="${val}" step="0.01" min="0" data-student="${s.studentId}" data-activity="${sc.activityId}" onchange="saveGrade(this)"></td>`;
            });
            const avg = s.average != null ? s.average + '%' : '—';
            html += `<td class="text-center fw-600">${avg}</td></tr>`;
        });
        html += '</tbody></table></div>';
        container.innerHTML = html;
    } catch (err) { container.innerHTML = `<div class="alert-bar error">${err.message}</div>`; }
}

async function saveGrade(input) {
    const studentId = input.dataset.student;
    const activityId = input.dataset.activity;
    const score = input.value !== '' ? parseFloat(input.value) : null;
    input.className = 'grade-input';
    try {
        await api.saveGrade(studentId, activityId, score);
        input.classList.add('saved');
        setTimeout(() => input.classList.remove('saved'), 1500);
        loadSpreadsheet(currentClassId);
    } catch (err) {
        input.classList.add('error');
        alert(err.message);
    }
}

function addActivityPrompt() {
    document.getElementById('activityName').value = '';
    document.getElementById('activityMaxScore').value = '100';
    new bootstrap.Modal(document.getElementById('activityModal')).show();
}

async function addActivity() {
    const name = document.getElementById('activityName').value.trim();
    const maxScore = document.getElementById('activityMaxScore').value;
    if (!name || !maxScore) return;
    try {
        await api.addActivity(currentClassId, name, maxScore);
        bootstrap.Modal.getInstance(document.getElementById('activityModal')).hide();
        loadSpreadsheet(currentClassId);
    } catch (err) { alert(err.message); }
}

async function deleteActivity(activityId) {
    if (!confirm('Delete this activity column? All grades for it will be removed.')) return;
    try { await api.deleteActivity(activityId); loadSpreadsheet(currentClassId); }
    catch (err) { alert(err.message); }
}

async function runSort() {
    if (!currentClassId) return;
    const card = document.getElementById('rankingsCard');
    const container = document.getElementById('rankingsContainer');
    card.style.display = 'block';
    container.innerHTML = '<div class="text-center" style="padding:2rem;"><div class="spinner"></div></div>';
    card.scrollIntoView({ behavior: 'smooth' });

    try {
        const res = await api.sortAndBonus(currentClassId);
        if (res.sortedStudents.length === 0) {
            container.innerHTML = '<div class="empty-state"><p>No grades to rank. Enter grades first.</p></div>';
            return;
        }

        let html = '<table class="data-table"><thead><tr><th>Rank</th><th>Student</th><th class="text-center">Average</th><th class="text-center">Bonus (+' + res.bonusAmount + ')</th><th class="text-center">Final Grade</th></tr></thead><tbody>';
        res.sortedStudents.forEach((s, i) => {
            const rank = i + 1;
            const rankCls = rank === 1 ? 'rank-1' : rank === 2 ? 'rank-2' : rank === 3 ? 'rank-3' : 'rank-default';
            const gradeBadge = s.finalGrade >= 90 ? 'badge-success' : s.finalGrade >= 75 ? 'badge-primary' : 'badge-danger';
            html += `<tr>
                <td><span class="rank-badge ${rankCls}">${rank}</span></td>
                <td class="fw-600">${s.fullName}</td>
                <td class="text-center">${s.originalGrade != null ? s.originalGrade.toFixed(2) + '%' : '—'}</td>
                <td class="text-center" style="color:var(--color-green-500);">+${res.bonusAmount}</td>
                <td class="text-center"><span class="badge ${gradeBadge}">${s.finalGrade != null ? s.finalGrade.toFixed(2) + '%' : '—'}</span></td>
            </tr>`;
        });
        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (err) { container.innerHTML = `<div class="alert-bar error">${err.message}</div>`; }
}
