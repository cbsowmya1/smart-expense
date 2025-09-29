package wip;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartexpense.workflow.dto.AttachmentRequest;
import com.smartexpense.workflow.dto.AttachmentResponse;
import com.smartexpense.workflow.dto.EvalReq;
import com.smartexpense.workflow.dto.EvalRes;
import com.smartexpense.workflow.dto.ExpenseCreateRequest;
import com.smartexpense.workflow.dto.ExpenseResponse;
import com.smartexpense.workflow.dto.ExpenseUpdateRequest;
import com.smartexpense.workflow.entities.Expense;
import com.smartexpense.workflow.entities.enums.ExpenseDecision;
import com.smartexpense.workflow.repositories.ExpenseRepository;
import com.smartexpense.workflow.services.util.DtoMapper;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
    private final ExpenseRepository repo;
    private final EvaluationService eval;
    private final PolicyService policyService;
    private final AuditService audit;
    private final AttachmentService attachments;
    private final JwtService jwt;
    private final ObjectMapper om;

    public ExpenseService(ExpenseRepository repo, EvaluationService eval, PolicyService policyService,
                          AuditService audit, AttachmentService attachments, JwtService jwt, ObjectMapper om) {
        this.repo = repo;
        this.eval = eval;
        this.policyService = policyService;
        this.audit = audit;
        this.attachments = attachments;
        this.jwt = jwt;
        this.om = om;
    }

    @Transactional
    public ExpenseResponse create(ExpenseCreateRequest req) {
        Long userId = jwt.currentUser().getId();

        Expense e = new Expense();
        e.setUserId(userId);
        e.setAmount(req.amount());
        e.setCurrency(req.currency());
        e.setCategory(req.category());
        e.setDescription(req.description());
        e.setReceiptUrl(req.receiptUrl());
        e.setDecision(ExpenseDecision.PENDING);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        repo.save(e);

        EvalReq call = new EvalReq(req.amount(), req.currency(), req.category(), req.description(), req.receiptUrl());

        CompletableFuture<EvalRes> f1 = eval.callAiAgent(call);
        CompletableFuture<Boolean> f2 = eval.checkDuplicate(userId, req.amount(), req.category());
        CompletableFuture<Boolean> f3 = eval.checkPolicy(req.amount(), req.category());

        CompletableFuture.allOf(f1, f2, f3).join();

        EvalRes res = f1.join();
        boolean duplicate = f2.join();
        boolean over = f3.join();

        List<String> violations = new ArrayList<>(res.violations() == null ? List.of() : res.violations());
        if (duplicate) violations.add("Duplicate expense detected");
        if (over) violations.add("Policy cap exceeded");

        e.setDecision(violations.isEmpty() ? ExpenseDecision.APPROVE : ExpenseDecision.REVIEW);
        try { e.setViolations(om.writeValueAsString(violations)); } catch (Exception ignored) {}
        e.setUpdatedAt(Instant.now());
        repo.save(e);

        audit.recordAction(e.getId(), userId, "EXPENSE_CREATED_EVALUATED", null, e.getViolations());
        return DtoMapper.toExpenseResponse(om, e);
    }

    public List<ExpenseResponse> list(String status, String category, String fromDate, String toDate) {
        Long userId = jwt.currentUser().getId();
        return repo.findByUserId(userId).stream().map(e -> DtoMapper.toExpenseResponse(om, e)).toList();
    }

    public ExpenseResponse get(Long id) {
        Expense e = repo.findById(id).orElseThrow(() -> new NotFoundException("Expense not found"));
        return DtoMapper.toExpenseResponse(om, e);
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseUpdateRequest req) {
        Expense e = repo.findById(id).orElseThrow(() -> new NotFoundException("Expense not found"));
        e.setCategory(req.category());
        e.setDescription(req.description());
        e.setReceiptUrl(req.receiptUrl());
        e.setUpdatedAt(Instant.now());
        repo.save(e);
        audit.recordAction(e.getId(), jwt.currentUser().getId(), "EXPENSE_UPDATED", null, e.getViolations());
        return DtoMapper.toExpenseResponse(om, e);
    }

    @Transactional
    public ExpenseResponse reEvaluate(Long id) {
        Expense e = repo.findById(id).orElseThrow(() -> new NotFoundException("Expense not found"));
        ExpenseCreateRequest req = new ExpenseCreateRequest(e.getAmount(), e.getCurrency(), e.getCategory(), e.getDescription(), e.getReceiptUrl());
        return create(req); // simple path, or refactor merge logic to reuse
    }

    public List<String> auditTrail(Long id) {
        return audit.listForExpense(id);
    }

    public AttachmentResponse addAttachment(Long id, AttachmentRequest req) {
        return attachments.addAttachment(id, req);
    }

    public List<AttachmentResponse> listAttachments(Long id) {
        return attachments.listAttachments(id);
    }
}
