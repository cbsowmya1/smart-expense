package com.smartexpense.workflow.dto;

public record AttachmentResponse(Long id, String fileName, String url, String mimeType, String uploadedAt) {}
