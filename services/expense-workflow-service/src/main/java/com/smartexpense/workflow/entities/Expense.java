// src/main/java/com/smartexpense/workflow/entities/Expense.java
package com.smartexpense.workflow.entities;

import java.math.BigDecimal;
import java.time.Instant;

import com.smartexpense.workflow.entities.enums.ExpenseDecision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;                // change to BigDecimal

    @Column(nullable = false, length = 8)
    private String currency = "INR";

    private String category;
    private String description;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseDecision decision = ExpenseDecision.PENDING;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String violations = "[]";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }         // getter returns BigDecimal
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getReceiptUrl() { return receiptUrl; }
    public ExpenseDecision getDecision() { return decision; }
    public String getViolations() { return violations; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }  // setter takes BigDecimal
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public void setDecision(ExpenseDecision decision) { this.decision = decision; }
    public void setViolations(String violations) { this.violations = violations; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
