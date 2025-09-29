package wip;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smartexpense.workflow.clients.AiAgentClient;
import com.smartexpense.workflow.dto.EvalReq;
import com.smartexpense.workflow.dto.EvalRes;
import com.smartexpense.workflow.repositories.ExpenseRepository;

@Service
public class EvaluationService {

    private final AiAgentClient aiClient;
    private final ExpenseRepository expenses;
    private final PolicyService policies;

    public EvaluationService(AiAgentClient aiClient, ExpenseRepository expenses, PolicyService policies) {
        this.aiClient = aiClient;
        this.expenses = expenses;
        this.policies = policies;
    }

    @Async
    public CompletableFuture<EvalRes> callAiAgent(EvalReq req) {
        return CompletableFuture.completedFuture(aiClient.evaluate(req));
    }

    @Async
    public CompletableFuture<Boolean> checkDuplicate(Long userId, Integer amount, String category) {
        Instant since = Instant.now().minus(1, ChronoUnit.DAYS);
        boolean exists = expenses.existsByUserIdAndAmountAndCategoryAndCreatedAtAfter(userId, amount, category, since);
        return CompletableFuture.completedFuture(exists);
    }

    @Async
    public CompletableFuture<Boolean> checkPolicy(Integer amount, String category) {
        return CompletableFuture.completedFuture(policies.isOverLimit(amount, category));
    }
}
