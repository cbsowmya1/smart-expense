package com.smartexpense.workflow.dto;

public record ViolationsRateItem(String month, Long total, Long violated, double rate) {}
