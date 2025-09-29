package com.smartexpense.workflow.dto;

import java.util.List;
import java.util.Map;

public record EvalRes(
	    String decision,
	    List<String> violations,
	    Map<String, Object> entities
	) {}
