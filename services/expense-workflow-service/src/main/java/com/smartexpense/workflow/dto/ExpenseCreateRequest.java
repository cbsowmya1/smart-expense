package com.smartexpense.workflow.dto;

import jakarta.validation.constraints.*;

public record ExpenseCreateRequest(
        @NotNull @Positive Integer amount,
        @NotBlank String currency,
        @NotBlank String category,
        @Size(max = 500) String description,
        String receiptUrl
) {}
