package com.smartexpense.workflow.dto;

public record ApprovalResponse(Long expenseId, Long approverId, String status, String comment, String decidedAt) {}
