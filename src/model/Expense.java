package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outgoing transaction tied to a category and a user.
 */
public class Expense {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDateTime occurredAt;
    private final String note;

    public Expense(UUID id, UUID userId, UUID categoryId, BigDecimal amount, String currency, LocalDateTime occurredAt, String note) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
        this.note = note;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getNote() {
        return note;
    }
}
