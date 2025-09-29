package com.smartexpense.workflow.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private Integer capAmount;
    private String currency;
    private boolean requiresReceipt;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private boolean active = true;

    // getters and setters
}
