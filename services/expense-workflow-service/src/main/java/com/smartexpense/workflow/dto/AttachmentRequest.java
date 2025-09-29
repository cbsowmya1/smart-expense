package com.smartexpense.workflow.dto;

import jakarta.validation.constraints.NotBlank;

public record AttachmentRequest(@NotBlank String url, @NotBlank String fileName, @NotBlank String mimeType) {}
