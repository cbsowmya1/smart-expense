package com.smartexpense.workflow.repositories;

import com.smartexpense.workflow.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    boolean existsByUserIdAndAmountAndCategoryAndCreatedAtAfter(Long userId, Integer amount, String category, Instant after);
}
