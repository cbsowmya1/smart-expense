package com.smartexpense.workflow.dto;

import java.time.LocalDate;

public record PolicyResponse(
        Long id,
        String category,
        Integer capAmount,
        String currency,
        boolean requiresReceipt,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        boolean active
) {}
