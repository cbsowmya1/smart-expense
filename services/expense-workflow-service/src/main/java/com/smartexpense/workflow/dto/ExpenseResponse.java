package com.smartexpense.workflow.dto;

import java.util.List;

public record ExpenseResponse(
        Long id,
        Long userId,
        Integer amount,
        String currency,
        String category,
        String description,
        String receiptUrl,
        String decision,
        List<String> violations,
        String createdAt,
        String updatedAt
) {}
