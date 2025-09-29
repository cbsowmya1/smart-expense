package com.smartexpense.workflow.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartexpense.workflow.dto.AttachmentRequest;
import com.smartexpense.workflow.dto.AttachmentResponse;
import com.smartexpense.workflow.dto.ExpenseCreateRequest;
import com.smartexpense.workflow.dto.ExpenseResponse;
import com.smartexpense.workflow.dto.ExpenseUpdateRequest;

import jakarta.validation.Valid;
import wip.ExpenseService;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenses;

    public ExpenseController(ExpenseService expenses) {
        this.expenses = expenses;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseCreateRequest request) {
        ExpenseResponse created = expenses.create(request);
        return ResponseEntity.created(URI.create("/expenses/" + created.id())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(expenses.list(status, category, fromDate, toDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(expenses.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ExpenseUpdateRequest request) {
        return ResponseEntity.ok(expenses.update(id, request));
    }

    @PostMapping("/{id}/re-evaluate")
    public ResponseEntity<ExpenseResponse> reEvaluate(@PathVariable Long id) {
        return ResponseEntity.ok(expenses.reEvaluate(id));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<String>> audit(@PathVariable Long id) {
        return ResponseEntity.ok(expenses.auditTrail(id));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<AttachmentResponse> addAttachment(@PathVariable Long id,
                                                            @Valid @RequestBody AttachmentRequest request) {
        return ResponseEntity.ok(expenses.addAttachment(id, request));
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> listAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(expenses.listAttachments(id));
    }
}
