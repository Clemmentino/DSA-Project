// GradeSecure — Audit Trail

const user = requireLogin();
if (user) { initSidebar(user, 'audit'); loadAuditLogs(); }

async function loadAuditLogs() {
    const container = document.getElementById('auditContainer');
    const filter = document.getElementById('auditFilter').value;
    try {
        const logs = await api.getAuditLogs(filter || null);
        document.getElementById('logCount').textContent = logs.length + ' events';

        if (logs.length === 0) {
            container.innerHTML = `<div class="empty-state">
                <div class="empty-icon"><i class="bi bi-clock-history"></i></div>
                <h4>No events recorded</h4>
                <p>Grade modifications will appear here automatically.</p>
            </div>`;
            return;
        }

        let html = '<table class="data-table"><thead><tr><th>Event</th><th>Table</th><th>Record</th><th>Changes</th><th>Timestamp</th></tr></thead><tbody>';
        logs.forEach(log => {
            const actionCls = `log-${log.actionType.toLowerCase()}`;
            const changes = log.actionType === 'DELETE' ? formatJson(log.oldValues) :
                            log.actionType === 'UPDATE' ? `${formatJson(log.oldValues)} → ${formatJson(log.newValues)}` :
                            formatJson(log.newValues);
            const time = log.performedAt ? new Date(log.performedAt).toLocaleString() : '—';
            html += `<tr>
                <td><span class="log-action ${actionCls}">${log.actionType}</span></td>
                <td><code style="font-size:0.75rem;background:var(--color-gray-100);padding:0.1rem 0.3rem;border-radius:3px;">${log.tableName}</code></td>
                <td>#${log.recordId}</td>
                <td style="font-size:0.75rem;color:var(--color-gray-600);max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${changes}</td>
                <td style="color:var(--color-gray-500);font-size:0.75rem;white-space:nowrap;">${time}</td>
            </tr>`;
        });
        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (err) { container.innerHTML = `<div class="alert-bar error">${err.message}</div>`; }
}

function formatJson(str) {
    try {
        const obj = typeof str === 'string' ? JSON.parse(str) : str;
        return Object.entries(obj).map(([k, v]) => `${k}: ${v}`).join(', ');
    } catch { return str || '—'; }
}
