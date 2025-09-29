package com.smartexpense.workflow.dto;

import jakarta.validation.constraints.NotBlank;

public record ApprovalActionRequest(@NotBlank String comment) {}
