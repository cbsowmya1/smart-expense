package wip;

import java.time.Instant;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.smartexpense.workflow.dto.AttachmentRequest;
import com.smartexpense.workflow.dto.AttachmentResponse;
import com.smartexpense.workflow.entities.Attachment;
import com.smartexpense.workflow.entities.Expense;
import com.smartexpense.workflow.repositories.AttachmentRepository;
import com.smartexpense.workflow.repositories.ExpenseRepository;

@Service
public class AttachmentService {
    private final AttachmentRepository repo;
    private final ExpenseRepository expenses;

    public AttachmentService(AttachmentRepository repo, ExpenseRepository expenses) {
        this.repo = repo;
        this.expenses = expenses;
    }

    public AttachmentResponse addAttachment(Long expenseId, AttachmentRequest r) {
        Expense e = expenses.findById(expenseId).orElseThrow(() -> new NotFoundException("Expense not found"));
        Attachment a = new Attachment();
        a.setExpense(e);
        a.setFileName(r.fileName());
        a.setUrl(r.url());
        a.setMimeType(r.mimeType());
        a.setUploadedAt(Instant.now());
        a = repo.save(a);
        return new AttachmentResponse(a.getId(), a.getFileName(), a.getUrl(), a.getMimeType(), a.getUploadedAt().toString());
    }

    public List<AttachmentResponse> listAttachments(Long expenseId) {
        return repo.findByExpenseId(expenseId).stream()
                .map(a -> new AttachmentResponse(a.getId(), a.getFileName(), a.getUrl(), a.getMimeType(), a.getUploadedAt().toString()))
                .toList();
    }
}
