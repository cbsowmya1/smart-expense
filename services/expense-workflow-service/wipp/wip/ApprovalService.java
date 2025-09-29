package wip;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartexpense.workflow.dto.ApprovalActionRequest;
import com.smartexpense.workflow.dto.ApprovalResponse;
import com.smartexpense.workflow.entities.Approval;
import com.smartexpense.workflow.entities.Expense;
import com.smartexpense.workflow.entities.enums.ApprovalStatus;
import com.smartexpense.workflow.entities.enums.ExpenseDecision;
import com.smartexpense.workflow.repositories.ApprovalRepository;
import com.smartexpense.workflow.repositories.ExpenseRepository;
import com.smartexpense.workflow.services.errors.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ApprovalService {
    private final ApprovalRepository approvals;
    private final ExpenseRepository expenses;
    private final AuditService audit;
    private final JwtService jwt;

    public ApprovalService(ApprovalRepository approvals, ExpenseRepository expenses, AuditService audit, JwtService jwt) {
        this.approvals = approvals;
        this.expenses = expenses;
        this.audit = audit;
        this.jwt = jwt;
    }

    public List<ApprovalResponse> inbox(String status, String category) {
        Long approverId = jwt.currentUser().getId();
        ApprovalStatus s = status == null ? ApprovalStatus.PENDING : ApprovalStatus.valueOf(status.toUpperCase());
        return approvals.findByApproverIdAndStatus(approverId, s)
                .stream()
                .map(a -> new ApprovalResponse(
                        a.getExpense().getId(),
                        a.getApproverId(),
                        a.getStatus().name(),
                        a.getComment(),
                        a.getDecidedAt() == null ? null : a.getDecidedAt().toString()))
                .toList();
    }

    @Transactional
    public ApprovalResponse approve(Long expenseId, ApprovalActionRequest req) {
        return decide(expenseId, ApprovalStatus.APPROVED, req == null ? null : req.comment());
    }

    @Transactional
    public ApprovalResponse reject(Long expenseId, ApprovalActionRequest req) {
        return decide(expenseId, ApprovalStatus.REJECTED, req == null ? null : req.comment());
    }

    private ApprovalResponse decide(Long expenseId, ApprovalStatus status, String comment) {
        Expense e = expenses.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        Approval a = new Approval();
        a.setExpense(e);
        a.setApproverId(jwt.currentUser().getId());
        a.setStatus(status);
        a.setComment(comment);
        a.setDecidedAt(Instant.now());
        approvals.save(a);

        e.setDecision(status == ApprovalStatus.APPROVED ? ExpenseDecision.APPROVE : ExpenseDecision.REJECT);
        expenses.save(e);

        audit.recordAction(e.getId(), a.getApproverId(), "APPROVAL_" + status.name(), null, comment);
        return new ApprovalResponse(
                e.getId(),
                a.getApproverId(),
                a.getStatus().name(),
                a.getComment(),
                a.getDecidedAt().toString());
    }
}
