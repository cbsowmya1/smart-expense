package com.smartexpense.workflow.dto;

import jakarta.validation.constraints.*;

public record ExpenseUpdateRequest(
        @NotBlank String category,
        @Size(max = 500) String description,
        String receiptUrl
) {}
