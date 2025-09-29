package com.smartexpense.workflow.dto;

public record SpendByCategoryItem(String category, String month, Long totalAmount, String currency) {}
