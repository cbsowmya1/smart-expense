package com.smartexpense.workflow.dto;

public record EvalReq(
	    Integer amount,
	    String currency,
	    String category,
	    String description,
	    String receiptUrl
	) {}