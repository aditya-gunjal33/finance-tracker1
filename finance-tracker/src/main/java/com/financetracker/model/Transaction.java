package com.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** "INCOME" or "EXPENSE" */
    @NotBlank(message = "Type is required")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Constructors ─────────────────────────────────────────────

    public Transaction() {}

    public Transaction(String title, BigDecimal amount, String type, String category) {
        this.title    = title;
        this.amount   = amount;
        this.type     = type;
        this.category = category;
    }

    // ── Getters / Setters ─────────────────────────────────────────

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getTitle()               { return title; }
    public void setTitle(String title)     { this.title = title; }

    public BigDecimal getAmount()          { return amount; }
    public void setAmount(BigDecimal a)    { this.amount = a; }

    public String getType()                { return type; }
    public void setType(String type)       { this.type = type; }

    public String getCategory()            { return category; }
    public void setCategory(String c)      { this.category = c; }

    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)     { this.createdAt = dt; }
}
