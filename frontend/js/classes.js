// GradeSecure — Classes & Roster

const user = requireLogin();
if (user) { initSidebar(user, 'classes'); loadClasses(); }

function showCreateClass() {
    document.getElementById('createClassCard').style.display = 'block';
    document.getElementById('className').focus();
}
function hideCreateClass() {
    document.getElementById('createClassCard').style.display = 'none';
}

document.getElementById('createClassForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('className').value.trim();
    if (!name) return;
    try {
        await api.createClass(name, user.id);
        document.getElementById('className').value = '';
        hideCreateClass();
        loadClasses();
    } catch (err) { alert(err.message); }
});

async function loadClasses() {
    const container = document.getElementById('classesList');
    try {
        const classes = await api.getClasses(user.role === 'TEACHER' ? user.id : null);
        if (classes.length === 0) {
            container.innerHTML = `<div class="empty-state" style="grid-column:1/-1;">
                <div class="empty-icon"><i class="bi bi-collection"></i></div>
                <h4>No classes yet</h4>
                <p>Create your first class to start managing student grades.</p>
                <button class="btn btn-primary btn-sm" onclick="showCreateClass()"><i class="bi bi-plus-lg"></i> New class</button>
            </div>`;
            return;
        }
        let html = '';
        classes.forEach(c => {
            html += `<div class="class-card" onclick="openRoster(${c.id}, '${c.name.replace(/'/g, "\\'")}')">
                <div style="display:flex;justify-content:space-between;align-items:flex-start;">
                    <div class="class-name">${c.name}</div>
                    <div class="dropdown" onclick="event.stopPropagation()">
                        <button class="btn btn-ghost btn-sm" data-bs-toggle="dropdown"><i class="bi bi-three-dots"></i></button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#" onclick="openAddStudent(${c.id})"><i class="bi bi-person-plus me-2"></i>Add students</a></li>
                            <li><a class="dropdown-item" href="spreadsheet.html?classId=${c.id}"><i class="bi bi-table me-2"></i>Open grades</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" style="color:var(--color-red-500)" href="#" onclick="deleteClass(${c.id})"><i class="bi bi-trash me-2"></i>Delete class</a></li>
                        </ul>
                    </div>
                </div>
                <div class="class-meta">
                    <span><i class="bi bi-person"></i> ${c.teacherName || 'Unassigned'}</span>
                    <span><i class="bi bi-people"></i> ${c.studentCount} students</span>
                </div>
            </div>`;
        });
        container.innerHTML = html;
    } catch (err) { container.innerHTML = `<div class="alert-bar error" style="grid-column:1/-1;">${err.message}</div>`; }
}

function openAddStudent(classId) {
    document.getElementById('addStudentClassId').value = classId;
    document.getElementById('studentName').value = '';
    document.getElementById('addStudentMsg').style.display = 'none';
    new bootstrap.Modal(document.getElementById('addStudentModal')).show();
    setTimeout(() => document.getElementById('studentName').focus(), 300);
}

async function addStudent() {
    const classId = document.getElementById('addStudentClassId').value;
    const input = document.getElementById('studentName');
    const name = input.value.trim();
    const msg = document.getElementById('addStudentMsg');
    if (!name) { input.focus(); return; }
    try {
        await api.addStudent(classId, name);
        msg.textContent = `✓ Added "${name}" — add another or click Done`;
        msg.style.display = 'flex';
        input.value = '';
        input.focus();
        loadClasses();
    } catch (err) {
        msg.textContent = err.message;
        msg.className = 'alert-bar error';
        msg.style.display = 'flex';
    }
}

document.getElementById('studentName')?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') { e.preventDefault(); addStudent(); }
});

async function openRoster(classId, className) {
    document.getElementById('rosterClassName').textContent = className;
    const body = document.getElementById('rosterBody');
    body.innerHTML = '<div class="text-center" style="padding:2rem;"><div class="spinner"></div></div>';
    new bootstrap.Modal(document.getElementById('rosterModal')).show();
    try {
        const students = await api.getStudents(classId);
        if (students.length === 0) {
            body.innerHTML = `<div class="empty-state">
                <div class="empty-icon"><i class="bi bi-people"></i></div>
                <h4>No students</h4>
                <p>Add students to this class to get started.</p>
                <button class="btn btn-primary btn-sm" onclick="bootstrap.Modal.getInstance(document.getElementById('rosterModal')).hide();openAddStudent(${classId})">Add students</button>
            </div>`;
            return;
        }
        let html = '<table class="data-table"><thead><tr><th>#</th><th>Student Name</th><th></th></tr></thead><tbody>';
        students.forEach((s, i) => {
            html += `<tr><td>${i+1}</td><td class="fw-600">${s.fullName}</td>
                <td class="text-right"><button class="btn btn-danger btn-sm" onclick="deleteStudent(${s.id},${classId},'${className.replace(/'/g,"\\'")}')"><i class="bi bi-trash"></i></button></td></tr>`;
        });
        html += '</tbody></table>';
        body.innerHTML = html;
    } catch (err) { body.innerHTML = `<div class="alert-bar error">${err.message}</div>`; }
}

async function deleteStudent(studentId, classId, className) {
    if (!confirm('Remove this student? Their grades will also be deleted.')) return;
    try { await api.deleteStudent(studentId); openRoster(classId, className); loadClasses(); }
    catch (err) { alert(err.message); }
}

async function deleteClass(classId) {
    if (!confirm('Delete this class? All students and grades will be permanently removed.')) return;
    try { await api.deleteClass(classId); loadClasses(); }
    catch (err) { alert(err.message); }
}
