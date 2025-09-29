package wip;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartexpense.workflow.entities.AuditLog;
import com.smartexpense.workflow.repositories.AuditLogRepository;

@Service
public class AuditService {
    private final AuditLogRepository logs;
    private final ObjectMapper om;

    public AuditService(AuditLogRepository logs, ObjectMapper om) {
        this.logs = logs;
        this.om = om;
    }

    public void recordAction(Long expenseId, Long userId, String action, Object oldVal, Object newVal) {
        AuditLog log = new AuditLog();
        log.setExpenseId(expenseId);
        log.setUserId(userId);
        log.setAction(action);
        log.setTimestamp(Instant.now());
        try {
            log.setOldValue(oldVal == null ? null : om.writeValueAsString(oldVal));
            log.setNewValue(newVal == null ? null : om.writeValueAsString(newVal));
        } catch (Exception ignored) {}
        logs.save(log);
    }

    public List<String> listForExpense(Long expenseId) {
        return logs.findByExpenseId(expenseId)
                .stream().map(AuditLog::getAction).toList();
    }
}
