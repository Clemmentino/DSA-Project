// ============================================
// DSA-GradeSecure — API Helper
// ============================================

const API_BASE = 'http://localhost:8080/api';

const api = {
    async request(method, endpoint, body = null) {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body) {
            options.body = JSON.stringify(body);
        }
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        if (!response.ok) {
            const error = await response.json().catch(() => ({ error: 'Request failed' }));
            throw new Error(error.error || error.message || 'Request failed');
        }
        // Check if response is JSON or blob
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return response;
    },

    get(endpoint) { return this.request('GET', endpoint); },
    post(endpoint, body) { return this.request('POST', endpoint, body); },
    put(endpoint, body) { return this.request('PUT', endpoint, body); },
    delete(endpoint) { return this.request('DELETE', endpoint); },

    // Auth
    login(username, password) {
        return this.post('/auth/login', { username, password });
    },

    // Classes
    getClasses(teacherId = null) {
        const param = teacherId ? `?teacherId=${teacherId}` : '';
        return this.get(`/classes${param}`);
    },
    createClass(name, teacherId) {
        return this.post('/classes', { name, teacherId });
    },
    deleteClass(id) {
        return this.delete(`/classes/${id}`);
    },

    // Students
    getStudents(classId) {
        return this.get(`/classes/${classId}/students`);
    },
    addStudent(classId, fullName) {
        return this.post(`/classes/${classId}/students`, { fullName });
    },
    deleteStudent(studentId) {
        return this.delete(`/classes/students/${studentId}`);
    },

    // Spreadsheet
    getSpreadsheet(classId) {
        return this.get(`/classes/${classId}/spreadsheet`);
    },
    addActivity(classId, name, maxScore) {
        return this.post(`/classes/${classId}/activities`, { name, maxScore });
    },
    updateActivity(activityId, name, maxScore) {
        return this.put(`/classes/activities/${activityId}`, { name, maxScore });
    },
    deleteActivity(activityId) {
        return this.delete(`/classes/activities/${activityId}`);
    },
    saveGrade(studentId, activityId, score) {
        return this.put('/classes/grades', { studentId, activityId, score });
    },
    exportCsv(classId) {
        return `${API_BASE}/classes/${classId}/export`;
    },

    // Sort
    sortAndBonus(classId) {
        return this.post(`/classes/${classId}/sort`);
    },

    // Audit logs
    getAuditLogs(table = null) {
        const param = table ? `?table=${table}` : '';
        return this.get(`/audit-logs${param}`);
    }
};
