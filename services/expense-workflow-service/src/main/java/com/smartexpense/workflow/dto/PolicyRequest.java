package com.smartexpense.workflow.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PolicyRequest(
        @NotBlank String category,
        @NotNull @Positive Integer capAmount,
        @NotBlank String currency,
        boolean requiresReceipt,
        @NotNull LocalDate effectiveFrom,
        LocalDate effectiveTo
) {}
